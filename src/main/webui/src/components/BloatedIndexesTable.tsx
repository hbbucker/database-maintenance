
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import { Button } from "@/components/ui/button";
import { RefreshCw, ArrowUpDown, Loader, X } from "lucide-react";
import { IndexInfo } from "@/types";
import React from "react";

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
  return (
    <div className="border rounded-lg">
      <ScrollArea className="h-[600px]">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Ação</TableHead>
              {columns.map(({ field, label }) => (
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
            ) : sortedIndexes.slice(0, 20).map((index, i) => {
                const statusItem = getStatusForIndex(index);
                const processing = isProcessing(statusItem);
                const failed = isFailed(statusItem);

                // Se está processando: mostra o ícone loader + tooltip.
                // Se falhou: mostra botão vermelho de ação e tooltip falha.
                // Caso contrário: mostra botão de ação.
                // Se está processando, botão de ação é oculto.

                return (
                  <TableRow key={i}>
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
                        {!processing && failed && (
                          <Tooltip>
                            <TooltipTrigger asChild>
                              <span>
                                <Button
                                  variant="destructive"
                                  size="icon"
                                  onClick={() => handleRecreateIndex(index)}
                                  disabled={recreateIndexPending}
                                  className="border-red-500 text-red-600"
                                >
                                  <X className="h-4 w-4" />
                                </Button>
                              </span>
                            </TooltipTrigger>
                            <TooltipContent>
                              <span>Falha: {statusItem?.status.status}</span>
                            </TooltipContent>
                          </Tooltip>
                        )}
                        {!processing && !failed && (
                          <Tooltip>
                            <TooltipTrigger asChild>
                              <span>
                                <Button
                                  variant="ghost"
                                  size="icon"
                                  onClick={() => handleRecreateIndex(index)}
                                  disabled={recreateIndexPending}
                                >
                                  <RefreshCw className="h-4 w-4" />
                                </Button>
                              </span>
                            </TooltipTrigger>
                            <TooltipContent>
                              <p>Recriar índice</p>
                            </TooltipContent>
                          </Tooltip>
                        )}
                      </TooltipProvider>
                    </TableCell>
                    <TableCell>{index.dataSource.name}</TableCell>
                    <TableCell>{index.schemaName.name}</TableCell>
                    <TableCell>{index.indexName.name}</TableCell>
                    <TableCell>{index.tableName.name}</TableCell>
                    <TableCell>{(index.bloatRatio.ratio * 100).toFixed(2)}%</TableCell>
                    <TableCell className="font-mono text-sm">{index.ddl.ddl}</TableCell>
                  </TableRow>
                )
            })}
          </TableBody>
        </Table>
      </ScrollArea>
    </div>
  );
}
