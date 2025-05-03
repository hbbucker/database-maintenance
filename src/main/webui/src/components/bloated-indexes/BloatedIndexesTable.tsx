import {Table, TableBody} from "@/components/ui/table";
import {ScrollArea} from "@/components/ui/scroll-area";
import {IndexInfo} from "@/types";
import {useState} from "react";
import {TableHeader} from "./TableHeader";
import {TableRow} from "./TableRow";

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
    {field: "dataSource", label: "Data Source"},
    {field: "schemaName", label: "Schema"},
    {field: "indexName", label: "Índice"},
    {field: "tableName", label: "Tabela"},
    {field: "bloatRatio", label: "Ratio"},
    {field: "indexSize", label: "Index Size"},
    {field: "tableSize", label: "Table Size"},
    {field: "totatIndexScan", label: "Total Scans"},
    {field: "lastTimeIndexUsed", label: "Último Uso"},
    {field: "totalIndexTuplesFetched", label: "Tuplas Obtidas"},
    {field: "totalIndexTuplesRead", label: "Tuplas Lidas"},
    {field: "ddl", label: "DDL"},
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

    return (
        // <div className="border rounded-lg bg-background overflow-x-auto ">
            <Table>
                <TableHeader
                    columns={columns}
                    sortField={sortField}
                    sortDirection={sortDirection}
                    handleSort={handleSort}
                />
                <TableBody>
                    {isLoading ? (
                        <tr>
                            <td colSpan={11} className="text-center p-4">Carregando...</td>
                        </tr>
                    ) : sortedIndexes.length === 0 ? (
                        <tr>
                            <td colSpan={11} className="text-center p-4">Nenhum índice encontrado.</td>
                        </tr>
                    ) : (
                        sortedIndexes.map((index, i) => {
                            const statusItem = getStatusForIndex(index);
                            const processing = isProcessing(statusItem);
                            const failed = isFailed(statusItem);

                            return (
                                <TableRow
                                    key={i}
                                    index={index}
                                    rowIndex={i}
                                    dialogOpenIndex={dialogOpenIndex}
                                    setDialogOpenIndex={setDialogOpenIndex}
                                    handleConfirmRecreate={handleConfirmRecreate}
                                    recreateIndexPending={recreateIndexPending}
                                    statusItem={statusItem}
                                    processing={processing}
                                    failed={failed}
                                />
                            );
                        })
                    )}
                </TableBody>
            </Table>
        // </div>
    );
}
