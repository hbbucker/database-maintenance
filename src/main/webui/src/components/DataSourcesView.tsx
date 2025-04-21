
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getDataSources, recreateAllIndexes } from "@/services/api";
import { useToast } from "@/hooks/use-toast";
import DataSourceTable from "./DataSourceTable";

export function DataSourcesView() {
  const { toast } = useToast();
  const queryClient = useQueryClient();

  const { data: dataSourcesData } = useQuery({
    queryKey: ["dataSources"],
    queryFn: getDataSources
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

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-semibold">Data Sources</h2>
      <DataSourceTable
        dataSources={dataSourcesData?.dataSources || []}
        recreateAll={(dataSource: string) => recreateAllMutation.mutate(dataSource)}
        recreateAllPending={recreateAllMutation.isPending}
      />
    </div>
  );
}
