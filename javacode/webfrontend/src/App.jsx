import React from 'react'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { Layout } from 'antd'
import MainLayout from './components/Layout/MainLayout'
import Dashboard from './pages/Dashboard'
import DataCatalog from './pages/DataCatalog'
import DataLineage from './pages/DataLineage'
import DataQuality from './pages/DataQuality'
import DataExplorer from './pages/DataExplorer'
import BusinessReports from './pages/BusinessReports'
import Metrics from './pages/Metrics'
import SchedulerManagement from './pages/SchedulerManagement'
import AssetTags from './pages/AssetTags'
import QualityRules from './pages/QualityRules'
import DatasourceManagement from './pages/DatasourceManagement'

function App() {
  return (
    <BrowserRouter>
      <MainLayout>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/catalog" element={<DataCatalog />} />
          <Route path="/lineage" element={<DataLineage />} />
          <Route path="/quality" element={<DataQuality />} />
          <Route path="/explorer" element={<DataExplorer />} />
          <Route path="/reports" element={<BusinessReports />} />
          <Route path="/metrics" element={<Metrics />} />
          <Route path="/scheduler" element={<SchedulerManagement />} />
          <Route path="/asset-tags" element={<AssetTags />} />
          <Route path="/quality-rules" element={<QualityRules />} />
          <Route path="/datasources" element={<DatasourceManagement />} />
        </Routes>
      </MainLayout>
    </BrowserRouter>
  )
}

export default App