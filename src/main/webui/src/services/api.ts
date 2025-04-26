import {IndexResponse, DataSourceResponse, IndexStatusResponse} from "@/types";

// Using relative URLs to work with the proxy in development mode
export async function getDataSources(): Promise<DataSourceResponse> {
  const response = await fetch(`/index-maintenance/datasources`);
  if (!response.ok) {
    throw new Error('Falha ao buscar data sources');
  }
  return response.json();
}

export async function getBloatedIndexes(dataSource: string): Promise<IndexResponse> {
  const response = await fetch(`/index-maintenance/${dataSource}/index/bloated`);
  if (!response.ok) {
    throw new Error('Falha ao buscar índices inchados');
  }
  return response.json();
}

export async function recreateIndex(dataSource: string, schemaName: string, indexName: string): Promise<void> {
  const response = await fetch(
    `/index-maintenance/${dataSource}/index/recreate/${schemaName}/${indexName}`,
    { method: 'POST' }
  );
  if (!response.ok) {
    throw new Error('Falha ao recriar índice');
  }
}

export async function recreateAllIndexes(dataSource: string): Promise<void> {
  const response = await fetch(
    `/index-maintenance/${dataSource}/index/recreate/all`,
    { method: 'POST' }
  );
  if (!response.ok) {
    throw new Error('Falha ao recriar todos os índices');
  }
}

export async function getIndexStatus(): Promise<IndexStatusResponse> {
  const response = await fetch(`/index-maintenance/index/status`);
  if (!response.ok) {
    throw new Error("Falha ao buscar status dos índices");
  }
  return response.json();
}
