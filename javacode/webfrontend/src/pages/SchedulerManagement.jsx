import React, { useEffect, useState } from 'react'
import { 
  Table, 
  Card, 
  Button, 
  Modal, 
  Form, 
  Input, 
  Select, 
  Space, 
  Tag, 
  Switch,
  message,
  Popconfirm,
  Spin,
  Collapse,
  Row,
  Col,
  InputNumber,
  Divider,
  Alert,
  AutoComplete
} from 'antd'
import { 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  PlayCircleOutlined, 
  PauseCircleOutlined,
  ReloadOutlined,
  InfoCircleOutlined
} from '@ant-design/icons'
import api from '../utils/api'

const { Option } = Select
const { Panel } = Collapse
const { TextArea } = Input

export default function SchedulerManagement() {
  const [loading, setLoading] = useState(false)
  const [jobs, setJobs] = useState([])
  const [modalVisible, setModalVisible] = useState(false)
  const [editingJob, setEditingJob] = useState(null)
  const [form] = Form.useForm()
  const [jobGroups, setJobGroups] = useState([])
  const [taskType, setTaskType] = useState('general') // general, db_scan, model_generate, metadata_upload
  const [datasources, setDatasources] = useState([])

  useEffect(() => {
    loadJobs()
    loadJobGroups()
    loadDatasources()
  }, [])

  const loadJobs = async () => {
    try {
      setLoading(true)
      const response = await api.get('/scheduler/jobs')
      // PageResult对象包含data字段，我们需要从中提取任务列表
      setJobs(response.data || [])
    } catch (error) {
      console.error('Failed to load jobs:', error)
      message.error('加载任务列表失败')
    } finally {
      setLoading(false)
    }
  }

  const loadJobGroups = async () => {
    try {
      // 这里可以调用获取任务组的API
      // 暂时使用模拟数据
      setJobGroups([
        { id: 1, name: '数据同步任务组' },
        { id: 2, name: '数据质量检查组' },
        { id: 3, name: '报表生成任务组' }
      ])
    } catch (error) {
      console.error('Failed to load job groups:', error)
    }
  }

  const loadDatasources = async () => {
    try {
      const response = await api.get('/datasources')
      setDatasources(response || [])
    } catch (error) {
      console.error('Failed to load datasources:', error)
    }
  }

  const handleCreate = () => {
    setEditingJob(null)
    setTaskType('general')
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record) => {
    setEditingJob(record)
    
    // 根据执行器handler判断任务类型
    if (record.executorHandler === 'mysql_metadata_scanner') {
      setTaskType('db_scan')
    } else if (record.executorHandler === 'dbt_model_generator') {
      setTaskType('model_generate')
    } else if (record.executorHandler === 'metadata_uploader') {
      setTaskType('metadata_upload')
    } else {
      setTaskType('general')
    }
    
    const formValues = {
      jobDesc: record.jobDesc,
      jobGroup: record.jobGroup,
      author: record.author,
      alarmEmail: record.alarmEmail,
      scheduleType: record.scheduleType,
      scheduleConf: record.scheduleConf,
      misfireStrategy: record.misfireStrategy,
      executorRouteStrategy: record.executorRouteStrategy,
      executorHandler: record.executorHandler,
      executorParam: record.executorParam,
      executorBlockStrategy: record.executorBlockStrategy,
      executorTimeout: record.executorTimeout,
      executorFailRetryCount: record.executorFailRetryCount,
      childJobid: record.childJobid,
      glueType: record.glueType,
      triggerStatus: record.triggerStatus === 1
    }
    
    // 解析特定任务类型的参数
    if ((record.executorHandler === 'mysql_metadata_scanner' || 
         record.executorHandler === 'dbt_model_generator' ||
         record.executorHandler === 'metadata_uploader') && 
        record.executorParam) {
      try {
        const params = JSON.parse(record.executorParam)
        if (record.executorHandler === 'mysql_metadata_scanner') {
          formValues.datasourceId = params.datasourceId
          formValues.outputPath = params.outputPath || 'metadata_scan_result.yaml'
        } else if (record.executorHandler === 'dbt_model_generator') {
          formValues.inputPath = params.inputPath || 'metadata_scan_result.yaml'
          formValues.modelsDir = params.modelsDir || 'models'
        } else if (record.executorHandler === 'metadata_uploader') {
          formValues.metadataFile = params.metadataFile || 'metadata_scan_result.yaml'
        }
      } catch (e) {
        console.log('解析参数失败', e)
      }
    }
    
    form.setFieldsValue(formValues)
    setModalVisible(true)
  }

  const handleDelete = async (id) => {
    try {
      await api.delete(`/scheduler/jobs/${id}`)
      message.success('删除成功')
      loadJobs()
    } catch (error) {
      console.error('Failed to delete job:', error)
      message.error('删除失败')
    }
  }

  const handleStart = async (id) => {
    try {
      await api.post(`/scheduler/jobs/${id}/start`)
      message.success('任务已启动')
      loadJobs()
    } catch (error) {
      console.error('Failed to start job:', error)
      message.error('启动任务失败')
    }
  }

  const handleStop = async (id) => {
    try {
      await api.post(`/scheduler/jobs/${id}/stop`)
      message.success('任务已停止')
      loadJobs()
    } catch (error) {
      console.error('Failed to stop job:', error)
      message.error('停止任务失败')
    }
  }

  const handleFinish = async (values) => {
    try {
      // 根据任务类型处理参数
      let submitValues = { ...values }
      
      if (taskType === 'db_scan') {
        // 数据库扫描任务
        submitValues.executorHandler = 'mysql_metadata_scanner'
        submitValues.executorParam = JSON.stringify({
          datasourceId: values.datasourceId,
          outputPath: values.outputPath || 'metadata_scan_result.yaml'
        })
      } else if (taskType === 'model_generate') {
        // Model生成任务
        submitValues.executorHandler = 'dbt_model_generator'
        submitValues.executorParam = JSON.stringify({
          inputPath: values.inputPath || 'metadata_scan_result.yaml',
          modelsDir: values.modelsDir || 'models'
        })
      } else if (taskType === 'metadata_upload') {
        // 元数据上传任务
        submitValues.executorHandler = 'metadata_uploader'
        submitValues.executorParam = JSON.stringify({
          metadataFile: values.metadataFile || 'metadata_scan_result.yaml'
        })
      }
      
      // 确保必需字段存在
      if (!submitValues.jobGroup) {
        message.error('请选择任务组')
        return
      }
      
      if (!submitValues.jobDesc) {
        message.error('请输入任务描述')
        return
      }
      
      if (!submitValues.author) {
        message.error('请输入负责人')
        return
      }
      
      if (!submitValues.scheduleType) {
        message.error('请选择调度类型')
        return
      }
      
      if (!submitValues.scheduleConf) {
        message.error('请输入调度配置')
        return
      }
      
      if (editingJob) {
        // 更新任务
        await api.put(`/scheduler/jobs/${editingJob.id}`, {
          ...submitValues,
          triggerStatus: submitValues.triggerStatus ? 1 : 0
        })
        message.success('更新成功')
      } else {
        // 创建任务
        await api.post('/scheduler/jobs', {
          ...submitValues,
          triggerStatus: submitValues.triggerStatus ? 1 : 0
        })
        message.success('创建成功')
      }
      setModalVisible(false)
      loadJobs()
    } catch (error) {
      console.error('Failed to save job:', error)
      message.error(editingJob ? '更新失败' : '创建失败')
    }
  }

  const getStatusTag = (status) => {
    if (status === 1) {
      return <Tag color="success">运行中</Tag>
    } else {
      return <Tag color="default">已停止</Tag>
    }
  }

  const getScheduleTypeText = (type) => {
    const types = {
      'CRON': 'Cron表达式',
      'FIX_RATE': '固定频率',
      'FIX_DELAY': '固定延迟'
    }
    return types[type] || type
  }

  const getTaskTypeText = (handler) => {
    if (handler === 'mysql_metadata_scanner') {
      return '数据库扫描'
    } else if (handler === 'dbt_model_generator') {
      return 'Model生成'
    } else if (handler === 'metadata_uploader') {
      return '元数据上传'
    }
    return '通用任务'
  }

  const columns = [
    {
      title: '任务描述',
      dataIndex: 'jobDesc',
      key: 'jobDesc'
    },
    {
      title: '任务组',
      dataIndex: 'jobGroup',
      key: 'jobGroup',
      render: (group) => {
        const groupObj = jobGroups.find(g => g.id === group)
        return groupObj ? groupObj.name : `组${group}`
      }
    },
    {
      title: '任务类型',
      dataIndex: 'executorHandler',
      key: 'executorHandler',
      render: (handler) => getTaskTypeText(handler)
    },
    {
      title: '调度类型',
      dataIndex: 'scheduleType',
      key: 'scheduleType',
      render: (type) => getScheduleTypeText(type)
    },
    {
      title: '调度配置',
      dataIndex: 'scheduleConf',
      key: 'scheduleConf'
    },
    {
      title: '负责人',
      dataIndex: 'author',
      key: 'author'
    },
    {
      title: '状态',
      dataIndex: 'triggerStatus',
      key: 'triggerStatus',
      render: (status) => getStatusTag(status)
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_, record) => (
        <Space size="middle">
          <Button 
            type="primary" 
            icon={<EditOutlined />} 
            size="small"
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          {record.triggerStatus === 1 ? (
            <Button 
              icon={<PauseCircleOutlined />} 
              size="small"
              onClick={() => handleStop(record.id)}
            >
              停止
            </Button>
          ) : (
            <Button 
              icon={<PlayCircleOutlined />} 
              size="small"
              onClick={() => handleStart(record.id)}
            >
              启动
            </Button>
          )}
          <Popconfirm
            title="确认删除"
            description="确定要删除这个任务吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确认"
            cancelText="取消"
          >
            <Button 
              danger 
              icon={<DeleteOutlined />} 
              size="small"
            >
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ]

  return (
    <div>
      <Card 
        style={{ marginBottom: 16 }}
        extra={
          <Space>
            <Button 
              type="primary" 
              icon={<PlusOutlined />}
              onClick={handleCreate}
            >
              新建任务
            </Button>
            <Button 
              icon={<ReloadOutlined />} 
              onClick={loadJobs}
            >
              刷新
            </Button>
          </Space>
        }
      >
        <Table
          columns={columns}
          dataSource={jobs}
          rowKey="id"
          loading={loading}
          pagination={{
            pageSize: 10,
            showTotal: (total) => `共 ${total} 条记录`
          }}
        />
      </Card>

      <Modal
        title={editingJob ? "编辑任务" : "新建任务"}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
        width={1000}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleFinish}
        >
          <Alert 
            message="任务类型说明" 
            description="您可以创建通用调度任务或专门的数据处理任务。数据处理任务分为三个步骤：数据库扫描、Model生成、元数据上传。" 
            type="info" 
            showIcon 
            style={{ marginBottom: 20 }}
          />
          
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="taskType"
                label="任务类型"
              >
                <Select 
                  placeholder="请选择任务类型" 
                  value={taskType}
                  onChange={(value) => setTaskType(value)}
                >
                  <Option value="general">通用任务</Option>
                  <Option value="db_scan">数据库扫描</Option>
                  <Option value="model_generate">Model生成</Option>
                  <Option value="metadata_upload">元数据上传</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>
          
          <Collapse defaultActiveKey={['1', '2', '3']} ghost>
            {/* 基本信息 */}
            <Panel header="基本信息" key="1">
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="jobDesc"
                    label="任务描述"
                    rules={[{ required: true, message: '请输入任务描述' }]}
                  >
                    <Input placeholder="请输入任务描述" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="jobGroup"
                    label="任务组"
                    rules={[{ required: true, message: '请选择任务组' }]}
                  >
                    <Select placeholder="请选择任务组">
                      {jobGroups.map(group => (
                        <Option key={group.id} value={group.id}>{group.name}</Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
              </Row>
              
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="author"
                    label="负责人"
                    rules={[{ required: true, message: '请输入负责人' }]}
                  >
                    <Input placeholder="请输入负责人" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="alarmEmail"
                    label="报警邮箱"
                  >
                    <Input placeholder="请输入报警邮箱" />
                  </Form.Item>
                </Col>
              </Row>
            </Panel>
            
            {/* 调度配置 */}
            <Panel header="调度配置" key="2">
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="scheduleType"
                    label="调度类型"
                    rules={[{ required: true, message: '请选择调度类型' }]}
                  >
                    <Select placeholder="请选择调度类型">
                      <Option value="CRON">Cron表达式</Option>
                      <Option value="FIX_RATE">固定频率</Option>
                      <Option value="FIX_DELAY">固定延迟</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="scheduleConf"
                    label="调度配置"
                    rules={[{ required: true, message: '请输入调度配置' }]}
                  >
                    <Input placeholder="请输入调度配置（如Cron表达式）" />
                  </Form.Item>
                </Col>
              </Row>
              
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="misfireStrategy"
                    label="调度过期策略"
                  >
                    <Select placeholder="请选择调度过期策略">
                      <Option value="DO_NOTHING">忽略</Option>
                      <Option value="FIRE_ONCE_NOW">立即执行一次</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    name="triggerStatus"
                    label="启用状态"
                    valuePropName="checked"
                  >
                    <Switch checkedChildren="启用" unCheckedChildren="停用" />
                  </Form.Item>
                </Col>
              </Row>
            </Panel>
            
            {/* 任务配置 */}
            <Panel header={taskType === 'db_scan' ? "数据库扫描配置" : 
                          taskType === 'model_generate' ? "Model生成配置" : 
                          taskType === 'metadata_upload' ? "元数据上传配置" : "执行器配置"} key="3">
              {taskType === 'db_scan' ? (
                // 数据库扫描任务配置
                <>
                  <Alert 
                    message="数据库扫描任务" 
                    description="此任务将扫描MySQL数据库的元数据并生成YAML文件。" 
                    type="info" 
                    showIcon 
                    style={{ marginBottom: 20 }}
                  />
                  
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="datasourceId"
                        label="数据源"
                        rules={[{ required: true, message: '请选择数据源' }]}
                      >
                        <Select 
                          placeholder="请选择数据源"
                          showSearch
                          optionFilterProp="children"
                        >
                          {datasources.map(ds => (
                            <Option key={ds.id} value={ds.id}>
                              {ds.name} ({ds.type})
                            </Option>
                          ))}
                        </Select>
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="outputPath"
                        label="输出文件路径"
                        initialValue="metadata_scan_result.yaml"
                      >
                        <Input placeholder="请输入输出文件路径" />
                      </Form.Item>
                    </Col>
                  </Row>
                  
                  <Divider orientation="left">高级配置</Divider>
                  
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="executorTimeout"
                        label="任务执行超时时间(秒)"
                        initialValue={300}
                      >
                        <InputNumber min={0} placeholder="请输入超时时间" style={{ width: '100%' }} />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="executorFailRetryCount"
                        label="失败重试次数"
                        initialValue={0}
                      >
                        <InputNumber min={0} placeholder="请输入重试次数" style={{ width: '100%' }} />
                      </Form.Item>
                    </Col>
                  </Row>
                </>
              ) : taskType === 'model_generate' ? (
                // Model生成任务配置
                <>
                  <Alert 
                    message="Model生成任务" 
                    description="此任务将根据扫描结果生成DBT模型文件。" 
                    type="info" 
                    showIcon 
                    style={{ marginBottom: 20 }}
                  />
                  
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="inputPath"
                        label="输入文件路径"
                        initialValue="metadata_scan_result.yaml"
                      >
                        <Input placeholder="请输入输入文件路径" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="modelsDir"
                        label="模型文件目录"
                        initialValue="models"
                      >
                        <Input placeholder="请输入模型文件目录" />
                      </Form.Item>
                    </Col>
                  </Row>
                  
                  <Divider orientation="left">高级配置</Divider>
                  
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="executorTimeout"
                        label="任务执行超时时间(秒)"
                        initialValue={300}
                      >
                        <InputNumber min={0} placeholder="请输入超时时间" style={{ width: '100%' }} />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="executorFailRetryCount"
                        label="失败重试次数"
                        initialValue={0}
                      >
                        <InputNumber min={0} placeholder="请输入重试次数" style={{ width: '100%' }} />
                      </Form.Item>
                    </Col>
                  </Row>
                </>
              ) : taskType === 'metadata_upload' ? (
                // 元数据上传任务配置
                <>
                  <Alert 
                    message="元数据上传任务" 
                    description="此任务将扫描结果上传到元数据服务。" 
                    type="info" 
                    showIcon 
                    style={{ marginBottom: 20 }}
                  />
                  
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="metadataFile"
                        label="元数据文件"
                        initialValue="metadata_scan_result.yaml"
                      >
                        <Input placeholder="请输入元数据文件路径" />
                      </Form.Item>
                    </Col>
                  </Row>
                  
                  <Divider orientation="left">高级配置</Divider>
                  
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="executorTimeout"
                        label="任务执行超时时间(秒)"
                        initialValue={300}
                      >
                        <InputNumber min={0} placeholder="请输入超时时间" style={{ width: '100%' }} />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="executorFailRetryCount"
                        label="失败重试次数"
                        initialValue={0}
                      >
                        <InputNumber min={0} placeholder="请输入重试次数" style={{ width: '100%' }} />
                      </Form.Item>
                    </Col>
                  </Row>
                </>
              ) : (
                // 通用任务配置
                <>
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="executorRouteStrategy"
                        label="路由策略"
                      >
                        <Select placeholder="请选择路由策略">
                          <Option value="FIRST">第一个</Option>
                          <Option value="LAST">最后一个</Option>
                          <Option value="ROUND">轮询</Option>
                          <Option value="RANDOM">随机</Option>
                          <Option value="CONSISTENT_HASH">一致性HASH</Option>
                          <Option value="LEAST_FREQUENTLY_USED">最不经常使用</Option>
                          <Option value="LEAST_RECENTLY_USED">最近最久未使用</Option>
                          <Option value="FAILOVER">故障转移</Option>
                          <Option value="BUSYOVER">忙碌转移</Option>
                          <Option value="SHARDING_BROADCAST">分片广播</Option>
                        </Select>
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="executorBlockStrategy"
                        label="阻塞处理策略"
                      >
                        <Select placeholder="请选择阻塞处理策略">
                          <Option value="SERIAL_EXECUTION">单机串行</Option>
                          <Option value="DISCARD_LATER">丢弃后续调度</Option>
                          <Option value="COVER_EARLY">覆盖之前调度</Option>
                        </Select>
                      </Form.Item>
                    </Col>
                  </Row>
                  
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="executorHandler"
                        label="执行器任务handler"
                      >
                        <Input placeholder="请输入执行器任务handler" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="executorParam"
                        label="执行器任务参数"
                      >
                        <TextArea placeholder="请输入执行器任务参数" rows={3} />
                      </Form.Item>
                    </Col>
                  </Row>
                  
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="executorTimeout"
                        label="任务执行超时时间(秒)"
                      >
                        <InputNumber min={0} placeholder="请输入超时时间" style={{ width: '100%' }} />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="executorFailRetryCount"
                        label="失败重试次数"
                      >
                        <InputNumber min={0} placeholder="请输入重试次数" style={{ width: '100%' }} />
                      </Form.Item>
                    </Col>
                  </Row>
                  
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="childJobid"
                        label="子任务ID"
                      >
                        <Input placeholder="多个ID用逗号分隔" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="glueType"
                        label="GLUE类型"
                      >
                        <Select placeholder="请选择GLUE类型">
                          <Option value="BEAN">BEAN</Option>
                          <Option value="GLUE_GROOVY">GLUE(Java)</Option>
                          <Option value="GLUE_SHELL">GLUE(Shell)</Option>
                          <Option value="GLUE_PYTHON">GLUE(Python)</Option>
                          <Option value="GLUE_PHP">GLUE(PHP)</Option>
                          <Option value="GLUE_NODEJS">GLUE(Nodejs)</Option>
                          <Option value="GLUE_POWERSHELL">GLUE(PowerShell)</Option>
                        </Select>
                      </Form.Item>
                    </Col>
                  </Row>
                </>
              )}
            </Panel>
          </Collapse>
          
          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                {editingJob ? '更新' : '创建'}
              </Button>
              <Button onClick={() => setModalVisible(false)}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}