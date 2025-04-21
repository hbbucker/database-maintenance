
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import { Button } from "@/components/ui/button";
import { Database } from "lucide-react";
import React from "react";
import { DataSourceInfo } from "@/types";

type Props = {
  dataSources: DataSourceInfo[];
  recreateAll: (dataSource: string) => void;
  recreateAllPending: boolean;
};

export default function DataSourceTable({ dataSources, recreateAll, recreateAllPending }: Props) {
  return (
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
          {dataSources.map((ds) => (
            <TableRow key={ds.dataSourceName}>
              <TableCell>
                <TooltipProvider>
                  <Tooltip>
                    <TooltipTrigger asChild>
                      <Button
                        variant="ghost"
                        className="flex items-center gap-2"
                        onClick={() => recreateAll(ds.dataSourceName)}
                        disabled={recreateAllPending}
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
  );
}
