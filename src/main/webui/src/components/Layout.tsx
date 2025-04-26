
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarProvider,
  SidebarTrigger,
} from "@/components/ui/sidebar";
import { Database, ListFilter } from "lucide-react";

interface LayoutProps {
  activeView: "indexes" | "datasources";
  onViewChange: (view: "indexes" | "datasources") => void;
  children: React.ReactNode;
}

export function Layout({ activeView, onViewChange, children }: LayoutProps) {
  return (
    <SidebarProvider>
      <div className="h-screen flex w-full">
        <Sidebar>
          <SidebarContent>
            <SidebarGroup>
              <SidebarGroupLabel className="pt-24">Navegação</SidebarGroupLabel>
              <SidebarGroupContent>
                <SidebarMenu className="pt-10">
                  <SidebarMenuItem>
                    <SidebarMenuButton
                      onClick={() => onViewChange("indexes")}
                      data-active={activeView === "indexes"}
                      tooltip="Índices Inchados"
                    >
                      <ListFilter className="h-4 w-4" />
                      <span>Índices Inchados</span>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                  <SidebarMenuItem>
                    <SidebarMenuButton
                      onClick={() => onViewChange("datasources")}
                      data-active={activeView === "datasources"}
                      tooltip="Data Sources"
                    >
                      <Database className="h-4 w-4" />
                      <span>Data Sources</span>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                </SidebarMenu>
              </SidebarGroupContent>
            </SidebarGroup>
          </SidebarContent>
        </Sidebar>
        <main className="flex-1 flex flex-col">
          <header className="bg-gradient-to-r from-[#7E69AB] to-[#9b87f5] text-white p-6 flex items-center justify-between shadow-md fixed top-0 left-0 right-0 z-50">
            <div className="flex items-center space-x-4">
              <Database className="h-8 w-8" />
              <h1 className="text-2xl font-bold tracking-wide">Database Maintenance</h1>
            </div>
            <SidebarTrigger className="hover:bg-purple-600 p-2 rounded-full transition-colors">
              <ListFilter className="h-6 w-6" />
            </SidebarTrigger>
          </header>
          <div className="mt-24 p-8 flex-1 flex flex-col overflow-hidden">
            {children}
          </div>
        </main>
      </div>
    </SidebarProvider>
  );
}
