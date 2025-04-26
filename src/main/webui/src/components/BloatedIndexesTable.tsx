import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import { Button } from "@/components/ui/button";
import { RefreshCw, Loader, X, ArrowUp, ArrowDown } from "lucide-react";
import { IndexInfo } from "@/types";
import React, { useState } from "react";
import { format } from "date-fns";
import {
  AlertDialog,
  AlertDialogTrigger,
  AlertDialogContent,
  AlertDialogHeader,
  AlertDialogFooter,
  AlertDialogTitle,
  AlertDialogDescription,
  AlertDialogAction,
  AlertDialogCancel
} from "@/components/ui/alert-dialog";

interface IndexStatusItem {
  dataSourceName: { name: string };
  tableName: { name: string };
  indexName: { name: string };
  status: { status: string };
}

type Props = {
  sortedIndexes: IndexInfo[];
  isLoading: boolean;
  handleSort: (field: string) => void;
  handleRecreateIndex: (index: IndexInfo) => void;
  recreateIndexPending: boolean;
  getStatusForIndex: (index: IndexInfo) => IndexStatusItem | undefined;
  isProcessing: (statusItem?: IndexStatusItem) => boolean;
  isFailed: (statusItem?: IndexStatusItem) => boolean;
  sortField?: string;
  sortDirection?: "asc" | "desc";
};

const columns = [
  { field: "dataSource", label: "Data Source" },
  { field: "schemaName", label: "Schema" },
  { field: "indexName", label: "Índice" },
  { field: "tableName", label: "Tabela" },
  { field: "bloatRatio", label: "Ratio" },
  { field: "tableSize", label: "Tam. Tabela" },
  { field: "indexSize", label: "Tam. Índice" },
  { field: "totatIndexScan", label: "Total Scans" },
  { field: "lastTimeIndexUsed", label: "Último Uso" },
  { field: "totalIndexTuplesFetched", label: "Tuplas Obtidas" },
  { field: "totalIndexTuplesRead", label: "Tuplas Lidas" },
  { field: "ddl", label: "DDL" },
];

export default function BloatedIndexesTable({
  sortedIndexes,
  isLoading,
  handleSort,
  handleRecreateIndex,
  recreateIndexPending,
  getStatusForIndex,
  isProcessing,
  isFailed,
  sortField,
  sortDirection
}: Props) {
  const [dialogOpenIndex, setDialogOpenIndex] = useState<number | null>(null);

  const handleConfirmRecreate = (index: IndexInfo) => {
    handleRecreateIndex(index);
    setDialogOpenIndex(null);
  };

  const formatDate = (dateString: string) => {
    return format(new Date(dateString), "yyyy-MM-dd HH:mm:ss");
  };

  const formatNumber = (num: number) => {
    return new Intl.NumberFormat().format(num);
  };

  return (
    <div className="border rounded-lg w-full h-full flex flex-col bg-background">
      <ScrollArea className="flex-1 w-full">
        <div className="min-w-[900px] w-full">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Ação</TableHead>
                {columns.map(({ field, label }) => (
                  <TableHead key={field} className="whitespace-nowrap">
                    <Button
                      variant="ghost"
                      onClick={() => handleSort(field)}
                      className="flex items-center gap-1"
                    >
                      {label}
                      {sortField === field && (
                        sortDirection === "asc" ? 
                          <ArrowUp className="ml-1 h-4 w-4" /> : 
                          <ArrowDown className="ml-1 h-4 w-4" />
                      )}
                    </Button>
                  </TableHead>
                ))}
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={13} className="text-center">Carregando...</TableCell>
                </TableRow>
              ) : sortedIndexes.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={13} className="text-center">Nenhum índice encontrado.</TableCell>
                </TableRow>
              ) : (
                sortedIndexes.map((index, i) => {
                  const statusItem = getStatusForIndex(index);
                  const processing = isProcessing(statusItem);
                  const failed = isFailed(statusItem);
                  return (
                    <TableRow key={i} className="hover:bg-muted transition">
                      <TableCell>
                        <TooltipProvider>
                          {processing && (
                            <Tooltip>
                              <TooltipTrigger asChild>
                                <span>
                                  <Loader className="h-5 w-5 animate-spin text-blue-600" />
                                </span>
                              </TooltipTrigger>
                              <TooltipContent>
                                <span>Status: {statusItem?.status.status}</span>
                              </TooltipContent>
                            </Tooltip>
                          )}
                          {!processing && (
                            <AlertDialog open={dialogOpenIndex === i} onOpenChange={open => setDialogOpenIndex(open ? i : null)}>
                              <AlertDialogTrigger asChild>
                                <span>
                                  <Tooltip>
                                    <TooltipTrigger asChild>
                                      {failed ? (
                                        <Button
                                          variant="destructive"
                                          size="icon"
                                          disabled={recreateIndexPending}
                                          className="border-red-500 text-red-600"
                                        >
                                          <X className="h-4 w-4" />
                                        </Button>
                                      ) : (
                                        <Button
                                          variant="ghost"
                                          size="icon"
                                          disabled={recreateIndexPending}
                                        >
                                          <RefreshCw className="h-4 w-4" />
                                        </Button>
                                      )}
                                    </TooltipTrigger>
                                    <TooltipContent>
                                      <span>
                                        {failed ? `Falha: ${statusItem?.status.status}` : "Recriar índice"}
                                      </span>
                                    </TooltipContent>
                                  </Tooltip>
                                </span>
                              </AlertDialogTrigger>
                              <AlertDialogContent>
                                <AlertDialogHeader>
                                  <AlertDialogTitle>Atenção: ação perigosa!</AlertDialogTitle>
                                  <AlertDialogDescription>
                                    Essa ação irá recriar o índice <span className="font-semibold">{index.indexName.name}</span> na tabela <span className="font-semibold">{index.tableName.name}</span>.<br/>
                                    Certifique-se de que não há dependências ou processos críticos em andamento.<br/>
                                    <span className="text-destructive font-bold">Essa ação é irreversível e pode impactar o banco de dados em produção.</span>
                                    <br/><br/>
                                    Deseja realmente continuar?
                                  </AlertDialogDescription>
                                </AlertDialogHeader>
                                <AlertDialogFooter>
                                  <AlertDialogCancel>Cancelar</AlertDialogCancel>
                                  <AlertDialogAction
                                    onClick={() => handleConfirmRecreate(index)}
                                    disabled={recreateIndexPending}
                                  >
                                    Confirmar e Executar
                                  </AlertDialogAction>
                                </AlertDialogFooter>
                              </AlertDialogContent>
                            </AlertDialog>
                          )}
                        </TooltipProvider>
                      </TableCell>
                      <TableCell className="whitespace-nowrap">{index.dataSource.name}</TableCell>
                      <TableCell className="whitespace-nowrap">{index.schemaName.name}</TableCell>
                      <TableCell className="whitespace-nowrap">{index.indexName.name}</TableCell>
                      <TableCell className="whitespace-nowrap">{index.tableName.name}</TableCell>
                      <TableCell className="whitespace-nowrap">{(index.bloatRatio.ratio * 100).toFixed(2)}%</TableCell>
                      <TableCell className="whitespace-nowrap">{formatNumber(index.tableSize.size)}</TableCell>
                      <TableCell className="whitespace-nowrap">{formatNumber(index.indexSize.size)}</TableCell>
                      <TableCell className="whitespace-nowrap">{formatNumber(index.totatIndexScan.value)}</TableCell>
                      <TableCell className="whitespace-nowrap">{formatDate(index.lastTimeIndexUsed.date)}</TableCell>
                      <TableCell className="whitespace-nowrap">{formatNumber(index.totalIndexTuplesFetched.value)}</TableCell>
                      <TableCell className="whitespace-nowrap">{formatNumber(index.totalIndexTuplesRead.value)}</TableCell>
                      <TableCell className="font-mono text-xs whitespace-pre-wrap max-w-md">{index.ddl.ddl}</TableCell>
                    </TableRow>
                  );
                })
              )}
            </TableBody>
          </Table>
        </div>
      </ScrollArea>
    </div>
  );
}
