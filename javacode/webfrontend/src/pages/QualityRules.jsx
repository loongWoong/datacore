import React, { useEffect, useState } from 'react'
import { 
  Table, 
  Card, 
  Button, 
  Modal, 
  Form, 
  Input, 
  Space, 
  Tag, 
  message,
  Popconfirm,
  Spin,
  Select,
  InputNumber
} from 'antd'
import { 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  ReloadOutlined
} from '@ant-design/icons'
import api from '../utils/api'

const { Option } = Select

export default function QualityRules() {
  const [loading, setLoading] = useState(false)
  const [rules, setRules] = useState([])
  const [modalVisible, setModalVisible] = useState(false)
  const [editingRule, setEditingRule] = useState(null)
  const [form] = Form.useForm()
  const [tables, setTables] = useState([])

  useEffect(() => {
    loadRules()
    loadTables()
  }, [])

  const loadRules = async () => {
    try {
      setLoading(true)
      // 这里应该调用获取质量规则的API
      // 暂时使用模拟数据
      const mockRules = [
        { 
          id: 1, 
          ruleName: '完整性检查', 
          ruleType: 'completeness', 
          tableName: 'toll_transaction', 
          columnName: 'transaction_id',
          threshold: 99.5,
          description: '检查交易ID字段的完整性'
        },
        { 
          id: 2, 
          ruleName: '唯一性检查', 
          ruleType: 'uniqueness', 
          tableName: 'toll_transaction', 
          columnName: 'transaction_id',
          threshold: 100,
          description: '检查交易ID字段的唯一性'
        },
        { 
          id: 3, 
          ruleName: '准确性检查', 
          ruleType: 'accuracy', 
          tableName: 'toll_transaction', 
          columnName: 'amount',
          threshold: 98,
          description: '检查金额字段的准确性'
        },
        { 
          id: 4, 
          ruleName: '一致性检查', 
          ruleType: 'consistency', 
          tableName: 'toll_station', 
          columnName: 'station_name',
          threshold: 95,
          description: '检查收费站名称的一致性'
        }
      ]
      setRules(mockRules)
    } catch (error) {
      console.error('Failed to load rules:', error)
      message.error('加载规则列表失败')
    } finally {
      setLoading(false)
    }
  }

  const loadTables = async () => {
    try {
      // 这里应该调用获取表列表的API
      // 暂时使用模拟数据
      const mockTables = [
        { tableName: 'toll_transaction', tableComment: '收费交易表' },
        { tableName: 'toll_station', tableComment: '收费站信息表' },
        { tableName: 'vehicle_info', tableComment: '车辆信息表' }
      ]
      setTables(mockTables)
    } catch (error) {
      console.error('Failed to load tables:', error)
    }
  }

  const handleCreate = () => {
    setEditingRule(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record) => {
    setEditingRule(record)
    form.setFieldsValue({
      ruleName: record.ruleName,
      ruleType: record.ruleType,
      tableName: record.tableName,
      columnName: record.columnName,
      threshold: record.threshold,
      description: record.description
    })
    setModalVisible(true)
  }

  const handleDelete = async (id) => {
    try {
      // 这里应该调用删除规则的API
      message.success('删除成功')
      loadRules()
    } catch (error) {
      console.error('Failed to delete rule:', error)
      message.error('删除失败')
    }
  }

  const handleFinish = async (values) => {
    try {
      if (editingRule) {
        // 更新规则
        message.success('更新成功')
      } else {
        // 创建规则
        message.success('创建成功')
      }
      setModalVisible(false)
      loadRules()
    } catch (error) {
      console.error('Failed to save rule:', error)
      message.error(editingRule ? '更新失败' : '创建失败')
    }
  }

  const getRuleTypeText = (type) => {
    const types = {
      'completeness': '完整性',
      'uniqueness': '唯一性',
      'accuracy': '准确性',
      'consistency': '一致性',
      'timeliness': '及时性'
    }
    return types[type] || type
  }

  const columns = [
    {
      title: '规则名称',
      dataIndex: 'ruleName',
      key: 'ruleName'
    },
    {
      title: '规则类型',
      dataIndex: 'ruleType',
      key: 'ruleType',
      render: (type) => getRuleTypeText(type)
    },
    {
      title: '表名',
      dataIndex: 'tableName',
      key: 'tableName'
    },
    {
      title: '字段名',
      dataIndex: 'columnName',
      key: 'columnName'
    },
    {
      title: '阈值(%)',
      dataIndex: 'threshold',
      key: 'threshold',
      render: (value) => `${value}%`
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description'
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
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
          <Popconfirm
            title="确认删除"
            description="确定要删除这个规则吗？"
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
              新建规则
            </Button>
            <Button 
              icon={<ReloadOutlined />} 
              onClick={loadRules}
            >
              刷新
            </Button>
          </Space>
        }
      >
        <Table
          columns={columns}
          dataSource={rules}
          rowKey="id"
          loading={loading}
          pagination={{
            pageSize: 10,
            showTotal: (total) => `共 ${total} 条记录`
          }}
        />
      </Card>

      <Modal
        title={editingRule ? "编辑规则" : "新建规则"}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleFinish}
        >
          <Form.Item
            name="ruleName"
            label="规则名称"
            rules={[{ required: true, message: '请输入规则名称' }]}
          >
            <Input placeholder="请输入规则名称" />
          </Form.Item>

          <Form.Item
            name="ruleType"
            label="规则类型"
            rules={[{ required: true, message: '请选择规则类型' }]}
          >
            <Select placeholder="请选择规则类型">
              <Option value="completeness">完整性</Option>
              <Option value="uniqueness">唯一性</Option>
              <Option value="accuracy">准确性</Option>
              <Option value="consistency">一致性</Option>
              <Option value="timeliness">及时性</Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="tableName"
            label="表名"
            rules={[{ required: true, message: '请选择表名' }]}
          >
            <Select 
              placeholder="请选择表名"
              showSearch
              optionFilterProp="children"
            >
              {tables.map(table => (
                <Option key={table.tableName} value={table.tableName}>
                  {table.tableName} ({table.tableComment})
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="columnName"
            label="字段名"
            rules={[{ required: true, message: '请输入字段名' }]}
          >
            <Input placeholder="请输入字段名" />
          </Form.Item>

          <Form.Item
            name="threshold"
            label="阈值(%)"
            rules={[{ required: true, message: '请输入阈值' }]}
          >
            <InputNumber 
              min={0} 
              max={100} 
              formatter={value => `${value}%`}
              parser={value => value.replace('%', '')}
              style={{ width: '100%' }}
            />
          </Form.Item>

          <Form.Item
            name="description"
            label="描述"
          >
            <Input.TextArea placeholder="请输入规则描述" rows={3} />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                {editingRule ? '更新' : '创建'}
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