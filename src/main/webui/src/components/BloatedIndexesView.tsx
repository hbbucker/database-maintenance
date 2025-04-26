import { RefreshCw } from "lucide-react";
import { Button } from "@/components/ui/button";
import BloatedIndexesTable from "./bloated-indexes/BloatedIndexesTable";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getBloatedIndexes, recreateIndex, getDataSources, getIndexStatus } from "@/services/api";
import { useToast } from "@/hooks/use-toast";
import { useState, useMemo, useEffect } from "react";
import {IndexInfo, IndexStatusItem} from "@/types";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

export function BloatedIndexesView() {
  const { toast } = useToast();
  const queryClient = useQueryClient();
  const [sortField, setSortField] = useState<string>("");
  const [sortDirection, setSortDirection] = useState<"asc" | "desc">("asc");
  const [indexStatus, setIndexStatus] = useState<IndexStatusItem[]>([]);
  const [selectedDataSource, setSelectedDataSource] = useState<string>("");

  const { data: dataSourcesData } = useQuery({
    queryKey: ["dataSources"],
    queryFn: getDataSources,
    onSuccess: (data) => {
      if (data?.dataSources?.length > 0) {
        setSelectedDataSource(data.dataSources[0].dataSourceName); // Set default to the first DataSource
      }
    },
  });

  useEffect(() => {
    let isMounted = true;
    let previousStatusMap = new Map<string, string>();

    const createStatusKey = (statusItem: IndexStatusItem) =>
      [statusItem.dataSourceName.name, statusItem.tableName.name, statusItem.indexName.name].join("__");

    const handleStatusChange = (statusItem: IndexStatusItem, previousStatus: string | undefined) => {
      if (/failed/i.test(statusItem.status.status)) {
        toast({
          title: "Erro",
          description: `Índice ${statusItem.indexName.name} falhou no processamento.`,
          variant: "destructive",
        });
      } else if (/sucessfully/i.test(statusItem.status.status)) {
        toast({
          title: "Sucesso",
          description: `Índice ${statusItem.indexName.name} processado com sucesso.`,
        });
      }
    };

    const updateStatusMap = (response: any) => {
      const newStatusMap = new Map<string, string>();
      response.indexProcessing.forEach((statusItem: IndexStatusItem) => {
        const key = createStatusKey(statusItem);
        newStatusMap.set(key, statusItem.status.status);

        const previousStatus = previousStatusMap.get(key);
        if (previousStatus !== statusItem.status.status) {
          handleStatusChange(statusItem, previousStatus);
        }
      });
      previousStatusMap = newStatusMap;
      setIndexStatus(response.indexProcessing || []);
    };

    const fetchStatus = async () => {
      try {
        const response = await getIndexStatus();
        if (isMounted) {
          updateStatusMap(response);
        }
      } catch {
        // Silently ignore errors
      }
    };

    fetchStatus();
    const interval = setInterval(fetchStatus, 5000);

    return () => {
      isMounted = false;
      clearInterval(interval);
    };
  }, [toast]);

  const indexStatusMap = useMemo(() => {
    const map = new Map<string, IndexStatusItem>();
    indexStatus.forEach((s) => {
      map.set(
        [s.dataSourceName.name, s.tableName.name, s.indexName.name].join("__"), s
      );
    });
    return map;
  }, [indexStatus]);

  const { data: bloatedIndexesData, isLoading, error, refetch } = useQuery({
    queryKey: ["bloatedIndexes", selectedDataSource],
    queryFn: async () => {
      if (!selectedDataSource) return { indexInfos: [] };
      return getBloatedIndexes(selectedDataSource);
    },
    enabled: !!selectedDataSource,
  });

  const sortedIndexes = useMemo(() => {
    if (!bloatedIndexesData?.indexInfos) return [];
    const indexes = [...bloatedIndexesData.indexInfos];
    if (sortField) {
      indexes.sort((a: any, b: any) => {
        // Handle numeric values properly
        if (sortField === 'totatIndexScan') {
          return sortDirection === 'asc' 
            ? a.totatIndexScan.value - b.totatIndexScan.value
            : b.totatIndexScan.value - a.totatIndexScan.value;
        }
        else if (sortField === 'totalIndexTuplesFetched') {
          return sortDirection === 'asc'
            ? a.totalIndexTuplesFetched.value - b.totalIndexTuplesFetched.value
            : b.totalIndexTuplesFetched.value - a.totalIndexTuplesFetched.value;
        }
        else if (sortField === 'totalIndexTuplesRead') {
          return sortDirection === 'asc'
            ? a.totalIndexTuplesRead.value - b.totalIndexTuplesRead.value
            : b.totalIndexTuplesRead.value - a.totalIndexTuplesRead.value;
        }
        else if (sortField === 'bloatRatio') {
          return sortDirection === 'asc'
            ? a.bloatRatio.ratio - b.bloatRatio.ratio
            : b.bloatRatio.ratio - a.bloatRatio.ratio;
        }
        else if (sortField === 'lastTimeIndexUsed') {
          return sortDirection === 'asc'
            ? new Date(a.lastTimeIndexUsed.date).getTime() - new Date(b.lastTimeIndexUsed.date).getTime()
            : new Date(b.lastTimeIndexUsed.date).getTime() - new Date(a.lastTimeIndexUsed.date).getTime();
        }
        else if (sortField === 'indexSize' || sortField === 'tableSize') {
          return sortDirection === 'asc'
            ? a[sortField].size - b[sortField].size
            : b[sortField].size - a[sortField].size;
        }
        
        // Default string comparison for other fields
        let aValue = a[sortField]?.name || a[sortField]?.ddl;
        let bValue = b[sortField]?.name || b[sortField]?.ddl;
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
        description: "Índice sendo recriado com sucesso",
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
    <div className="flex flex-col h-full">
      <div className="flex justify-between items-center mb-4">
        <div className="flex items-center gap-4">
          <h2 className="text-xl font-semibold">Índices Inchados</h2>
          <Select onValueChange={setSelectedDataSource} value={selectedDataSource}>
            <SelectTrigger className="w-64">
              <SelectValue placeholder="Selecione um DataSource" />
            </SelectTrigger>
            <SelectContent>
              {dataSourcesData?.dataSources.map((ds) => (
                <SelectItem key={ds.dataSourceName} value={ds.dataSourceName}>
                  {ds.dataSourceName}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
        <Button onClick={() => refetch()} variant="outline" size="sm">
          <RefreshCw className="h-4 w-4 mr-2" />
          Atualizar Lista
        </Button>
      </div>
      <div className="flex-1 min-h-0">
        <BloatedIndexesTable
          sortedIndexes={sortedIndexes}
          isLoading={isLoading}
          handleSort={handleSort}
          handleRecreateIndex={handleRecreateIndex}
          recreateIndexPending={recreateIndexMutation.isPending}
          getStatusForIndex={getStatusForIndex}
          isProcessing={isProcessing}
          isFailed={isFailed}
          sortField={sortField}
          sortDirection={sortDirection}
        />
      </div>
    </div>
  );
}

