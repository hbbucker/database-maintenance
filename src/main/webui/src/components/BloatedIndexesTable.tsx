
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import { Button } from "@/components/ui/button";
import { RefreshCw, Loader, X } from "lucide-react";
import { IndexInfo } from "@/types";
import React, { useState } from "react";
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
};

const columns = [
  { field: "dataSource", label: "Data Source" },
  { field: "schemaName", label: "Schema" },
  { field: "indexName", label: "Índice" },
  { field: "tableName", label: "Tabela" },
  { field: "bloatRatio", label: "Ratio" },
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
}: Props) {
  const [dialogOpenIndex, setDialogOpenIndex] = useState<number | null>(null);

  // handler para confirmar ação perigosa
  const handleConfirmRecreate = (index: IndexInfo) => {
    handleRecreateIndex(index);
    setDialogOpenIndex(null);
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
              ) : sortedIndexes.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={7} className="text-center">Nenhum índice encontrado.</TableCell>
                </TableRow>
              ) : sortedIndexes.map((index, i) => {
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
                      <TableCell className="font-mono text-xs max-w-xs truncate">{index.ddl.ddl}</TableCell>
                    </TableRow>
                  );
              })}
            </TableBody>
          </Table>
        </div>
      </ScrollArea>
    </div>
  );
}
