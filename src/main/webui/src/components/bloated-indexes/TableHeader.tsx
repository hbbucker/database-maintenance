import { Button } from "@/components/ui/button";
import { TableHead, TableHeader as UITableHeader, TableRow } from "@/components/ui/table";
import { ArrowDown, ArrowUp } from "lucide-react";

interface Column {
  field: string;
  label: string;
}

interface TableHeaderProps {
  columns: Column[];
  sortField?: string;
  sortDirection?: "asc" | "desc";
  handleSort: (field: string) => void;
}

export function TableHeader({ columns, sortField, sortDirection, handleSort }: TableHeaderProps) {
  return (
    <UITableHeader className="sticky top-0 bg-background">
      <TableRow>
        <TableHead className="sticky left-0 bg-background">Ação</TableHead>
        {columns.map(({ field, label }) => (
          <TableHead
            key={field}
            className={`whitespace-nowrap ${["bloatRatio", "indexSize", "tableSize", "totatIndexScan", "totalIndexTuplesFetched", "totalIndexTuplesRead"].includes(field) ? "text-right" : ""}`}
          >
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
    </UITableHeader>
  );
}
