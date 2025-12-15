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
  Select
} from 'antd'
import { 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  ReloadOutlined
} from '@ant-design/icons'
import api from '../utils/api'

const { Option } = Select

export default function AssetTags() {
  const [loading, setLoading] = useState(false)
  const [tags, setTags] = useState([])
  const [modalVisible, setModalVisible] = useState(false)
  const [editingTag, setEditingTag] = useState(null)
  const [form] = Form.useForm()
  const [assets, setAssets] = useState([])

  useEffect(() => {
    loadTags()
  }, [])

  const loadTags = async () => {
    try {
      setLoading(true)
      // 这里应该调用获取标签的API
      // 暂时使用模拟数据
      const mockTags = [
        { id: 1, tagName: '核心资产', tagColor: 'red', description: '最重要的数据资产' },
        { id: 2, tagName: '敏感数据', tagColor: 'orange', description: '包含敏感信息的数据' },
        { id: 3, tagName: '公开数据', tagColor: 'green', description: '可对外公开的数据' },
        { id: 4, tagName: '财务数据', tagColor: 'blue', description: '财务相关的数据' },
        { id: 5, tagName: '运营数据', tagColor: 'purple', description: '运营管理相关数据' }
      ]
      setTags(mockTags)
      
      // 模拟资产数据
      const mockAssets = [
        { id: 1, assetName: '用户信息表' },
        { id: 2, assetName: '交易记录表' },
        { id: 3, assetName: '财务报表' }
      ]
      setAssets(mockAssets)
    } catch (error) {
      console.error('Failed to load tags:', error)
      message.error('加载标签列表失败')
    } finally {
      setLoading(false)
    }
  }

  const handleCreate = () => {
    setEditingTag(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record) => {
    setEditingTag(record)
    form.setFieldsValue({
      tagName: record.tagName,
      tagColor: record.tagColor,
      description: record.description
    })
    setModalVisible(true)
  }

  const handleDelete = async (id) => {
    try {
      // 这里应该调用删除标签的API
      message.success('删除成功')
      loadTags()
    } catch (error) {
      console.error('Failed to delete tag:', error)
      message.error('删除失败')
    }
  }

  const handleFinish = async (values) => {
    try {
      if (editingTag) {
        // 更新标签
        message.success('更新成功')
      } else {
        // 创建标签
        message.success('创建成功')
      }
      setModalVisible(false)
      loadTags()
    } catch (error) {
      console.error('Failed to save tag:', error)
      message.error(editingTag ? '更新失败' : '创建失败')
    }
  }

  const getColorTag = (color, text) => {
    return <Tag color={color}>{text}</Tag>
  }

  const columns = [
    {
      title: '标签名称',
      dataIndex: 'tagName',
      key: 'tagName',
      render: (text, record) => getColorTag(record.tagColor, text)
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
            description="确定要删除这个标签吗？"
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
              新建标签
            </Button>
            <Button 
              icon={<ReloadOutlined />} 
              onClick={loadTags}
            >
              刷新
            </Button>
          </Space>
        }
      >
        <Table
          columns={columns}
          dataSource={tags}
          rowKey="id"
          loading={loading}
          pagination={{
            pageSize: 10,
            showTotal: (total) => `共 ${total} 条记录`
          }}
        />
      </Card>

      <Modal
        title={editingTag ? "编辑标签" : "新建标签"}
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
            name="tagName"
            label="标签名称"
            rules={[{ required: true, message: '请输入标签名称' }]}
          >
            <Input placeholder="请输入标签名称" />
          </Form.Item>

          <Form.Item
            name="tagColor"
            label="标签颜色"
            rules={[{ required: true, message: '请选择标签颜色' }]}
          >
            <Select placeholder="请选择标签颜色">
              <Option value="red">红色</Option>
              <Option value="orange">橙色</Option>
              <Option value="yellow">黄色</Option>
              <Option value="green">绿色</Option>
              <Option value="cyan">青色</Option>
              <Option value="blue">蓝色</Option>
              <Option value="purple">紫色</Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="description"
            label="描述"
          >
            <Input.TextArea placeholder="请输入标签描述" rows={3} />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                {editingTag ? '更新' : '创建'}
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