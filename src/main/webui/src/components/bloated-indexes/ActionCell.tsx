
import { Button } from "@/components/ui/button";
import { IndexInfo } from "@/types";
import { Loader, RefreshCw, X } from "lucide-react";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import {
  AlertDialog,
  AlertDialogTrigger,
  AlertDialogContent,
  AlertDialogHeader,
  AlertDialogFooter,
  AlertDialogTitle,
  AlertDialogDescription,
  AlertDialogAction,
  AlertDialogCancel,
} from "@/components/ui/alert-dialog";

interface ActionCellProps {
  index: IndexInfo;
  dialogOpenIndex: number | null;
  setDialogOpenIndex: (index: number | null) => void;
  rowIndex: number;
  handleConfirmRecreate: (index: IndexInfo) => void;
  recreateIndexPending: boolean;
  statusItem?: { status: { status: string } };
  processing: boolean;
  failed: boolean;
}

export function ActionCell({
  index,
  dialogOpenIndex,
  setDialogOpenIndex,
  rowIndex,
  handleConfirmRecreate,
  recreateIndexPending,
  statusItem,
  processing,
  failed,
}: ActionCellProps) {
  if (processing) {
    return (
      <TooltipProvider>
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
      </TooltipProvider>
    );
  }

  return (
    <AlertDialog open={dialogOpenIndex === rowIndex} onOpenChange={open => setDialogOpenIndex(open ? rowIndex : null)}>
      <AlertDialogTrigger asChild>
        <span>
          <TooltipProvider>
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
          </TooltipProvider>
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
  );
}
