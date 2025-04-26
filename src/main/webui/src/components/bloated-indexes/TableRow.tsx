
import { TableCell, TableRow as UITableRow } from "@/components/ui/table";
import { IndexInfo } from "@/types";
import { ActionCell } from "./ActionCell";
import { format } from "date-fns";

interface TableRowProps {
  index: IndexInfo;
  rowIndex: number;
  dialogOpenIndex: number | null;
  setDialogOpenIndex: (index: number | null) => void;
  handleConfirmRecreate: (index: IndexInfo) => void;
  recreateIndexPending: boolean;
  statusItem?: { status: { status: string } };
  processing: boolean;
  failed: boolean;
}

export function TableRow({
  index,
  rowIndex,
  dialogOpenIndex,
  setDialogOpenIndex,
  handleConfirmRecreate,
  recreateIndexPending,
  statusItem,
  processing,
  failed,
}: TableRowProps) {
  const formatDate = (dateString: string) => {
    return format(new Date(dateString), "yyyy-MM-dd HH:mm:ss");
  };

  const formatNumber = (num: number) => {
    return new Intl.NumberFormat().format(num);
  };

  const formatSize = (num: number) => {
     return new Intl.NumberFormat().format(num/1024/1024) + " MB";
  };

  return (
    <UITableRow className="hover:bg-muted transition">
      <TableCell>
        <ActionCell
          index={index}
          dialogOpenIndex={dialogOpenIndex}
          setDialogOpenIndex={setDialogOpenIndex}
          rowIndex={rowIndex}
          handleConfirmRecreate={handleConfirmRecreate}
          recreateIndexPending={recreateIndexPending}
          statusItem={statusItem}
          processing={processing}
          failed={failed}
        />
      </TableCell>
      <TableCell className="whitespace-nowrap">{index.dataSource.name}</TableCell>
      <TableCell className="whitespace-nowrap">{index.schemaName.name}</TableCell>
      <TableCell className="whitespace-nowrap">{index.indexName.name}</TableCell>
      <TableCell className="whitespace-nowrap">{index.tableName.name}</TableCell>
      <TableCell className="whitespace-nowrap text-right">{(index.bloatRatio.ratio * 100).toFixed(2)}%</TableCell>
      <TableCell className="whitespace-nowrap text-right">{formatSize(index.indexSize.size)}</TableCell>
      <TableCell className="whitespace-nowrap text-right">{formatSize(index.tableSize.size)}</TableCell>
      <TableCell className="whitespace-nowrap text-right">{formatNumber(index.totatIndexScan.value)}</TableCell>
      <TableCell className="whitespace-nowrap">{formatDate(index.lastTimeIndexUsed.date)}</TableCell>
      <TableCell className="whitespace-nowrap text-right">{formatNumber(index.totalIndexTuplesFetched.value)}</TableCell>
      <TableCell className="whitespace-nowrap text-right">{formatNumber(index.totalIndexTuplesRead.value)}</TableCell>
      <TableCell className="font-mono text-xs whitespace-pre-wrap max-w-md">{index.ddl.ddl}</TableCell>
    </UITableRow>
  );
}
