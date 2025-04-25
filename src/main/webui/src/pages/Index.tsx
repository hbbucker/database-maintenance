
import { useState } from "react";
import { Layout } from "@/components/Layout";
import { BloatedIndexesView } from "@/components/BloatedIndexesView";
import { DataSourcesView } from "@/components/DataSourcesView";
import Footer from "@/components/Footer";

const IndexMaintenancePage = () => {
  const [activeView, setActiveView] = useState<"indexes" | "datasources">("indexes");

  return (
    <Layout activeView={activeView} onViewChange={setActiveView}>
      <div className="flex flex-col h-[calc(100vh-4rem)]">
        <div className="flex-grow overflow-hidden flex flex-col">
          {activeView === "indexes" ? (
            <BloatedIndexesView />
          ) : (
            <DataSourcesView />
          )}
        </div>
        <Footer />
      </div>
    </Layout>
  );
};

export default IndexMaintenancePage;
