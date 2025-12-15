import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, Select, message, Space, Popconfirm } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, CheckCircleOutlined } from '@ant-design/icons';

const { Option } = Select;

const DatasourceManagement = () => {
  const [datasources, setDatasources] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingDatasource, setEditingDatasource] = useState(null);
  const [form] = Form.useForm();

  // 获取数据源列表
  const fetchDatasources = async () => {
    setLoading(true);
    try {
      // 模拟API调用
      const response = await fetch('/api/datasources');
      const data = await response.json();
      setDatasources(data);
    } catch (error) {
      message.error('获取数据源列表失败');
    } finally {
      setLoading(false);
    }
  };

  // 测试数据源连接
  const testConnection = async (id) => {
    try {
      const response = await fetch(`/api/datasources/${id}/test-connection`, {
        method: 'POST'
      });
      const result = await response.json();
      if (result) {
        message.success('连接测试成功');
      } else {
        message.error('连接测试失败');
      }
    } catch (error) {
      message.error('连接测试失败');
    }
  };

  // 删除数据源
  const deleteDatasource = async (id) => {
    try {
      const response = await fetch(`/api/datasources/${id}`, {
        method: 'DELETE'
      });
      if (response.ok) {
        message.success('删除成功');
        fetchDatasources();
      } else {
        message.error('删除失败');
      }
    } catch (error) {
      message.error('删除失败');
    }
  };

  // 提交表单
  const onFinish = async (values) => {
    try {
      if (editingDatasource) {
        // 更新数据源
        const response = await fetch(`/api/datasources/${editingDatasource.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(values),
        });
        if (response.ok) {
          message.success('更新成功');
        } else {
          message.error('更新失败');
        }
      } else {
        // 创建数据源
        const response = await fetch('/api/datasources', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(values),
        });
        if (response.ok) {
          message.success('创建成功');
        } else {
          message.error('创建失败');
        }
      }
      setModalVisible(false);
      form.resetFields();
      fetchDatasources();
    } catch (error) {
      message.error('操作失败');
    }
  };

  // 编辑数据源
  const editDatasource = (record) => {
    setEditingDatasource(record);
    form.setFieldsValue(record);
    setModalVisible(true);
  };

  // 新建数据源
  const createDatasource = () => {
    setEditingDatasource(null);
    form.resetFields();
    setModalVisible(true);
  };

  useEffect(() => {
    fetchDatasources();
  }, []);

  const columns = [
    {
      title: '数据源名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
    },
    {
      title: '主机地址',
      dataIndex: 'host',
      key: 'host',
    },
    {
      title: '端口',
      dataIndex: 'port',
      key: 'port',
    },
    {
      title: '数据库名称',
      dataIndex: 'databaseName',
      key: 'databaseName',
    },
    {
      title: '负责人',
      dataIndex: 'owner',
      key: 'owner',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (
        <span>
          {status === 'ACTIVE' ? (
            <CheckCircleOutlined style={{ color: '#52c41a' }} />
          ) : (
            <CheckCircleOutlined style={{ color: '#ff4d4f' }} />
          )}
          <span style={{ marginLeft: 8 }}>{status === 'ACTIVE' ? '启用' : '禁用'}</span>
        </span>
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <Button 
            type="primary" 
            icon={<EditOutlined />} 
            onClick={() => editDatasource(record)}
            size="small"
          >
            编辑
          </Button>
          <Button 
            onClick={() => testConnection(record.id)}
            size="small"
          >
            测试连接
          </Button>
          <Popconfirm
            title="确定要删除这个数据源吗？"
            onConfirm={() => deleteDatasource(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button 
              type="danger" 
              icon={<DeleteOutlined />} 
              size="small"
            >
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <h2>数据源管理</h2>
        <Button 
          type="primary" 
          icon={<PlusOutlined />}
          onClick={createDatasource}
        >
          新建数据源
        </Button>
      </div>
      
      <Table 
        columns={columns} 
        dataSource={datasources} 
        loading={loading}
        rowKey="id"
        pagination={{
          pageSize: 10,
        }}
      />
      
      <Modal
        title={editingDatasource ? "编辑数据源" : "新建数据源"}
        visible={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          form.resetFields();
        }}
        footer={null}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
        >
          <Form.Item
            name="name"
            label="数据源名称"
            rules={[{ required: true, message: '请输入数据源名称' }]}
          >
            <Input placeholder="请输入数据源名称" />
          </Form.Item>
          
          <Form.Item
            name="type"
            label="数据源类型"
            rules={[{ required: true, message: '请选择数据源类型' }]}
          >
            <Select placeholder="请选择数据源类型">
              <Option value="MYSQL">MySQL</Option>
              <Option value="POSTGRESQL">PostgreSQL</Option>
              <Option value="ORACLE">Oracle</Option>
              <Option value="SQLSERVER">SQL Server</Option>
            </Select>
          </Form.Item>
          
          <Form.Item
            name="host"
            label="主机地址"
            rules={[{ required: true, message: '请输入主机地址' }]}
          >
            <Input placeholder="请输入主机地址" />
          </Form.Item>
          
          <Form.Item
            name="port"
            label="端口号"
            rules={[{ required: true, message: '请输入端口号' }]}
          >
            <Input type="number" placeholder="请输入端口号" />
          </Form.Item>
          
          <Form.Item
            name="databaseName"
            label="数据库名称"
            rules={[{ required: true, message: '请输入数据库名称' }]}
          >
            <Input placeholder="请输入数据库名称" />
          </Form.Item>
          
          <Form.Item
            name="username"
            label="用户名"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input placeholder="请输入用户名" />
          </Form.Item>
          
          <Form.Item
            name="password"
            label="密码"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password placeholder="请输入密码" />
          </Form.Item>
          
          <Form.Item
            name="owner"
            label="负责人"
          >
            <Input placeholder="请输入负责人" />
          </Form.Item>
          
          <Form.Item
            name="description"
            label="描述"
          >
            <Input.TextArea placeholder="请输入描述" rows={3} />
          </Form.Item>
          
          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                {editingDatasource ? '更新' : '创建'}
              </Button>
              <Button onClick={() => setModalVisible(false)}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default DatasourceManagement;