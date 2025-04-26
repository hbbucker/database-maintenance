export interface DataSource {
  name: string;
}

export interface SchemaName {
  name: string;
}

export interface IndexName {
  name: string;
}

export interface TableName {
  name: string;
}

export interface BloatRatio {
  ratio: number;
}

export interface DDL {
  ddl: string;
}

export interface DataSourceInfo {
  dataSourceName: string;
  dbType: string;
  host: string;
  port: number;
  database: string;
}

export interface DataSourceResponse {
  dataSources: DataSourceInfo[];
}

export interface TotalIndexScan {
  value: number;
}

export interface LastTimeIndexUsed {
  date: string;
}

export interface TotalIndexTuplesFetched {
  value: number;
}

export interface TotalIndexTuplesRead {
  value: number;
}

export interface IndexSize {
  size: number;
}

export interface TableSize {
  size: number;
}

export interface IndexInfo {
  dataSource: DataSource;
  schemaName: SchemaName;
  indexName: IndexName;
  tableName: TableName;
  bloatRatio: BloatRatio;
  ddl: DDL;
  totatIndexScan: TotalIndexScan;
  lastTimeIndexUsed: LastTimeIndexUsed;
  totalIndexTuplesFetched: TotalIndexTuplesFetched;
  totalIndexTuplesRead: TotalIndexTuplesRead;
  indexSize: IndexSize;
  tableSize: TableSize;
}

export interface IndexResponse {
  indexInfos: IndexInfo[];
}
