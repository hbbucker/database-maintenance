
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import { Button } from "@/components/ui/button";
import { Database, RefreshCw, ArrowUpDown } from "lucide-react";
import { getBloatedIndexes, recreateIndex, recreateAllIndexes, getDataSources } from "@/services/api";
import { useToast } from "@/hooks/use-toast";
import { IndexInfo } from "@/types";
import { useState, useMemo } from "react";

const IndexMaintenancePage = () => {
  const { toast } = useToast();
  const queryClient = useQueryClient();
  const [sortField, setSortField] = useState<string>("");
  const [sortDirection, setSortDirection] = useState<"asc" | "desc">("asc");

  const { data: dataSourcesData } = useQuery({
    queryKey: ["dataSources"],
    queryFn: getDataSources
  });

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

  const recreateAllMutation = useMutation({
    mutationFn: (dataSource: string) => recreateAllIndexes(dataSource),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["bloatedIndexes"] });
      toast({
        title: "Sucesso",
        description: "Todos os índices foram recriados com sucesso",
      });
    },
    onError: () => {
      toast({
        title: "Erro",
        description: "Falha ao recriar índices",
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

  if (error) return <div>Erro ao carregar dados</div>;

  return (
    <div className="container mx-auto py-8">
      <h1 className="text-2xl font-bold mb-6">Manutenção de Índices</h1>

      <div className="space-y-8">
        <div>
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-semibold">Índices Inchados</h2>
            <Button onClick={() => refetch()} variant="outline" size="sm">
              <RefreshCw className="h-4 w-4 mr-2" />
              Atualizar Lista
            </Button>
          </div>
          <div className="border rounded-lg">
            <ScrollArea className="h-[600px]">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Ação</TableHead>
                    {[
                      { field: "dataSource", label: "Data Source" },
                      { field: "schemaName", label: "Schema" },
                      { field: "indexName", label: "Índice" },
                      { field: "tableName", label: "Tabela" },
                      { field: "bloatRatio", label: "Ratio" },
                      { field: "ddl", label: "DDL" }
                    ].map(({ field, label }) => (
                      <TableHead key={field}>
                        <Button
                          variant="ghost"
                          onClick={() => handleSort(field)}
                          className="flex items-center gap-1"
                        >
                          {label}
                          <ArrowUpDown className="h-4 w-4" />
                        </Button>
                      </TableHead>
                    ))}
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {isLoading ? (
                    <TableRow>
                      <TableCell colSpan={7} className="text-center">Carregando...</TableCell>
                    </TableRow>
                  ) : sortedIndexes.map((index, i) => (
                    <TableRow key={i}>
                      <TableCell>
                        <TooltipProvider>
                          <Tooltip>
                            <TooltipTrigger asChild>
                              <Button
                                variant="ghost"
                                size="icon"
                                onClick={() => handleRecreateIndex(index)}
                                disabled={recreateIndexMutation.isPending}
                              >
                                <RefreshCw className="h-4 w-4" />
                              </Button>
                            </TooltipTrigger>
                            <TooltipContent>
                              <p>Recriar índice</p>
                            </TooltipContent>
                          </Tooltip>
                        </TooltipProvider>
                      </TableCell>
                      <TableCell>{index.dataSource.name}</TableCell>
                      <TableCell>{index.schemaName.name}</TableCell>
                      <TableCell>{index.indexName.name}</TableCell>
                      <TableCell>{index.tableName.name}</TableCell>
                      <TableCell>{(index.bloatRatio.ratio * 100).toFixed(2)}%</TableCell>
                      <TableCell className="font-mono text-sm">{index.ddl.ddl}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </ScrollArea>
          </div>
        </div>

        <div>
          <h2 className="text-xl font-semibold mb-4">Data Sources</h2>
          <div className="border rounded-lg">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Ação</TableHead>
                  <TableHead>Data Source Name</TableHead>
                  <TableHead>DB Type</TableHead>
                  <TableHead>Database</TableHead>
                  <TableHead>Host</TableHead>
                  <TableHead>Port</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {dataSourcesData?.dataSources.map((ds) => (
                  <TableRow key={ds.dataSourceName}>
                    <TableCell>
                      <TooltipProvider>
                        <Tooltip>
                          <TooltipTrigger asChild>
                            <Button
                              variant="ghost"
                              className="flex items-center gap-2"
                              onClick={() => recreateAllMutation.mutate(ds.dataSourceName)}
                              disabled={recreateAllMutation.isPending}
                            >
                              <Database className="h-4 w-4" />
                              <span>Recriar Todos</span>
                            </Button>
                          </TooltipTrigger>
                          <TooltipContent>
                            <p>Recriar todos os índices inchados</p>
                          </TooltipContent>
                        </Tooltip>
                      </TooltipProvider>
                    </TableCell>
                    <TableCell>{ds.dataSourceName}</TableCell>
                    <TableCell>{ds.dbType}</TableCell>
                    <TableCell>{ds.database}</TableCell>
                    <TableCell>{ds.host}</TableCell>
                    <TableCell>{ds.port}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default IndexMaintenancePage;
