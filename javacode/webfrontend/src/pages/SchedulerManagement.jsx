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
  Spin
} from 'antd'
import { 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  PlayCircleOutlined, 
  PauseCircleOutlined,
  ReloadOutlined
} from '@ant-design/icons'
import api from '../utils/api'

const { Option } = Select

export default function SchedulerManagement() {
  const [loading, setLoading] = useState(false)
  const [jobs, setJobs] = useState([])
  const [modalVisible, setModalVisible] = useState(false)
  const [editingJob, setEditingJob] = useState(null)
  const [form] = Form.useForm()
  const [jobGroups, setJobGroups] = useState([])

  useEffect(() => {
    loadJobs()
    loadJobGroups()
  }, [])

  const loadJobs = async () => {
    try {
      setLoading(true)
      const data = await api.get('/scheduler/jobs')
      setJobs(data.records || [])
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

  const handleCreate = () => {
    setEditingJob(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record) => {
    setEditingJob(record)
    form.setFieldsValue({
      jobDesc: record.jobDesc,
      jobGroup: record.jobGroup,
      author: record.author,
      alarmEmail: record.alarmEmail,
      scheduleType: record.scheduleType,
      scheduleConf: record.scheduleConf,
      cronExpression: record.cronExpression,
      misfireStrategy: record.misfireStrategy,
      executorRouteStrategy: record.executorRouteStrategy,
      childJobId: record.childJobId,
      executorBlockStrategy: record.executorBlockStrategy,
      executorTimeout: record.executorTimeout,
      executorFailRetryCount: record.executorFailRetryCount,
      glueType: record.glueType,
      glueSource: record.glueSource,
      glueRemark: record.glueRemark,
      triggerStatus: record.triggerStatus === 1
    })
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
      if (editingJob) {
        // 更新任务
        await api.put(`/scheduler/jobs/${editingJob.id}`, {
          ...values,
          triggerStatus: values.triggerStatus ? 1 : 0
        })
        message.success('更新成功')
      } else {
        // 创建任务
        await api.post('/scheduler/jobs', {
          ...values,
          triggerStatus: values.triggerStatus ? 1 : 0
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
      title: '调度类型',
      dataIndex: 'scheduleType',
      key: 'scheduleType',
      render: (type) => getScheduleTypeText(type)
    },
    {
      title: 'Cron表达式',
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
        width={800}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleFinish}
        >
          <Form.Item
            name="jobDesc"
            label="任务描述"
            rules={[{ required: true, message: '请输入任务描述' }]}
          >
            <Input placeholder="请输入任务描述" />
          </Form.Item>

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

          <Form.Item
            name="author"
            label="负责人"
            rules={[{ required: true, message: '请输入负责人' }]}
          >
            <Input placeholder="请输入负责人" />
          </Form.Item>

          <Form.Item
            name="alarmEmail"
            label="报警邮箱"
          >
            <Input placeholder="请输入报警邮箱" />
          </Form.Item>

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

          <Form.Item
            name="scheduleConf"
            label="调度配置"
            rules={[{ required: true, message: '请输入调度配置' }]}
          >
            <Input placeholder="请输入调度配置（如Cron表达式）" />
          </Form.Item>

          <Form.Item
            name="triggerStatus"
            label="启用状态"
            valuePropName="checked"
          >
            <Switch checkedChildren="启用" unCheckedChildren="停用" />
          </Form.Item>

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