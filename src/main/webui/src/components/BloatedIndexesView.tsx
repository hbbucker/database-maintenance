
import { RefreshCw } from "lucide-react";
import { Button } from "@/components/ui/button";
import BloatedIndexesTable from "./BloatedIndexesTable";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getBloatedIndexes, recreateIndex, getDataSources } from "@/services/api";
import { useToast } from "@/hooks/use-toast";
import { useState, useMemo, useEffect } from "react";
import { IndexInfo } from "@/types";

interface IndexStatusItem {
  dataSourceName: { name: string };
  tableName: { name: string };
  indexName: { name: string };
  status: { status: string };
}

interface IndexStatusResponse {
  indexProcessing: IndexStatusItem[];
}

export function BloatedIndexesView() {
  const { toast } = useToast();
  const queryClient = useQueryClient();
  const [sortField, setSortField] = useState<string>("");
  const [sortDirection, setSortDirection] = useState<"asc" | "desc">("asc");
  const [indexStatus, setIndexStatus] = useState<IndexStatusItem[]>([]);

  const { data: dataSourcesData } = useQuery({
    queryKey: ["dataSources"],
    queryFn: getDataSources
  });

  useEffect(() => {
    let isMounted = true;
    async function fetchStatus() {
      try {
        const response = await fetch("/index-maintenance/index/status");
        if (response.ok) {
          const json: IndexStatusResponse = await response.json();
          if (isMounted) setIndexStatus(json.indexProcessing || []);
        }
      } catch (e) {
        // ignora erros silenciosamente
      }
    }
    fetchStatus();
    const interval = setInterval(fetchStatus, 5000);
    return () => {
      isMounted = false;
      clearInterval(interval);
    };
  }, []);

  const indexStatusMap = useMemo(() => {
    const map = new Map<string, IndexStatusItem>();
    indexStatus.forEach((s) => {
      map.set(
        [s.dataSourceName.name, s.tableName.name, s.indexName.name].join("__"),
        s
      );
    });
    return map;
  }, [indexStatus]);

  const { data: bloatedIndexesData, isLoading, error, refetch } = useQuery({
    queryKey: ["bloatedIndexes"],
    queryFn: async () => {
      if (!dataSourcesData?.dataSources) return { indexInfos: [] };
      const results = await Promise.all(
        dataSourcesData.dataSources.map(ds => 
          getBloatedIndexes(ds.dataSourceName)
        )
      );
      return {
        indexInfos: results.flatMap(result => result.indexInfos)
      };
    },
    enabled: !!dataSourcesData?.dataSources
  });

  const sortedIndexes = useMemo(() => {
    if (!bloatedIndexesData?.indexInfos) return [];
    const indexes = [...bloatedIndexesData.indexInfos];
    if (sortField) {
      indexes.sort((a: any, b: any) => {
        let aValue = a[sortField]?.name || a[sortField]?.ratio || a[sortField]?.ddl;
        let bValue = b[sortField]?.name || b[sortField]?.ratio || b[sortField]?.ddl;
        if (typeof aValue === 'number') {
          return sortDirection === 'asc' ? aValue - bValue : bValue - aValue;
        }
        return sortDirection === 'asc' 
          ? String(aValue).localeCompare(String(bValue))
          : String(bValue).localeCompare(String(aValue));
      });
    }
    return indexes;
  }, [bloatedIndexesData, sortField, sortDirection]);

  const recreateIndexMutation = useMutation({
    mutationFn: ({ dataSource, schemaName, indexName }: { dataSource: string; schemaName: string; indexName: string; }) =>
      recreateIndex(dataSource, schemaName, indexName),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["bloatedIndexes"] });
      toast({
        title: "Sucesso",
        description: "Índice recriado com sucesso",
      });
    },
    onError: () => {
      toast({
        title: "Erro",
        description: "Falha ao recriar índice",
        variant: "destructive",
      });
    },
  });

  const handleSort = (field: string) => {
    setSortDirection(current => field === sortField && current === "asc" ? "desc" : "asc");
    setSortField(field);
  };

  const handleRecreateIndex = (index: IndexInfo) => {
    recreateIndexMutation.mutate({
      dataSource: index.dataSource.name,
      schemaName: index.schemaName.name,
      indexName: index.indexName.name,
    });
  };

  function getStatusForIndex(index: IndexInfo): IndexStatusItem | undefined {
    const key = [
      index.dataSource.name,
      index.tableName.name,
      index.indexName.name,
    ].join("__");
    return indexStatusMap.get(key);
  }

  function isProcessing(statusItem?: IndexStatusItem): boolean {
    return !!statusItem && !/failed/i.test(statusItem.status.status);
  }

  function isFailed(statusItem?: IndexStatusItem): boolean {
    return !!statusItem && /failed/i.test(statusItem.status.status);
  }

  if (error) return <div>Erro ao carregar dados</div>;

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-xl font-semibold">Índices Inchados</h2>
        <Button onClick={() => refetch()} variant="outline" size="sm">
          <RefreshCw className="h-4 w-4 mr-2" />
          Atualizar Lista
        </Button>
      </div>
      <BloatedIndexesTable
        sortedIndexes={sortedIndexes}
        isLoading={isLoading}
        handleSort={handleSort}
        handleRecreateIndex={handleRecreateIndex}
        recreateIndexPending={recreateIndexMutation.isPending}
        getStatusForIndex={getStatusForIndex}
        isProcessing={isProcessing}
        isFailed={isFailed}
      />
    </div>
  );
}
