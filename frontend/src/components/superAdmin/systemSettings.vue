<template>
  <el-container style="margin: 0 0 60px;">
    <el-main class="out-box-white">
      <el-container style="margin-bottom: 20px; margin-left: 10px">
        <h2 style="text-align: left">系统设置</h2>
      </el-container>

      <el-form :model="registerForm" :rules="rules" v-loading="loading" :ref="registerForm" size="small" class="box-form">
        <el-row>
          <el-col :span="4">
            <span class="span-title">新图书管理员账号</span>
            <el-divider direction="vertical"/>
          </el-col>
          <el-col :span="4">
            <el-form-item prop="username" class="box-input">
              <el-input type="text" v-model="registerForm.username" auto-complete="off" placeholder="新管理员用户名"/>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item prop="password" class="box-input">
              <el-input type="password" v-model="registerForm.password" autocomplete="off" placeholder="新管理员密码"/>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item prop="checkPassword" class="box-input">
              <el-input type="password" v-model="registerForm.checkPassword" autocomplete="off" placeholder="确认密码"/>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item prop="email" class="box-input">
              <el-input type="text" v-model="registerForm.email" auto-complete="off" placeholder="新管理员邮箱地址"/>
            </el-form-item>
          </el-col>
          <el-col :span="2">&nbsp;</el-col>
          <el-col :span="2">
            <el-button type="primary" icon="el-icon-plus" v-on:click="submitForm(registerForm)" style="margin-top: -5px">添加</el-button>
          </el-col>
        </el-row>
      </el-form>

      <el-divider/>

      <el-form ref="setMaxCopiesForm" :model="setMaxCopiesForm" class="box-form">
        <el-row>
          <el-col :span="4">
            <span class="span-title">设置最大借阅本数</span>
            <el-divider direction="vertical"/>
          </el-col>
          <el-col :span="3">
            <el-select @change="handleMaxCopiesChange" v-model="setMaxCopiesForm.select" placeholder="请选择">
              <el-option label="教师" value="Teacher"/>
              <el-option label="本科生" value="Undergraduate"/>
              <el-option label="研究生" value="Postgraduate"/>
            </el-select>
          </el-col>
          <el-col :span="15">
            <el-input-number style="margin-top: 3px" size="small" min="-0.1" max="1000.1" :step="1" step-strictly
                             v-model="setMaxCopiesForm.input"/> 本
          </el-col>
          <el-col :span="2">
            <el-button type="primary" icon="el-icon-edit" @click="submitSetMaxCopiesForm()"> 更新 </el-button>
          </el-col>
        </el-row>
      </el-form>

      <el-divider/>

      <el-form ref="setMaxReserveTimeForm" :model="setMaxReserveTimeForm" class="box-form">
        <el-row>
          <el-col :span="4">
            <span class="span-title">设置最长预约时间</span>
            <el-divider direction="vertical"/>
          </el-col>
          <el-col :span="3">
            <el-select @change="handleMaxReserveTimeChange" v-model="setMaxReserveTimeForm.select" placeholder="请选择">
              <el-option label="教师" value="Teacher"/>
              <el-option label="本科生" value="Undergraduate"/>
              <el-option label="研究生" value="Postgraduate"/>
            </el-select>
          </el-col>
          <el-col :span="15">
            <el-input-number style="margin-top: 3px" size="small" min="-0.1" max="365.1" :step="1" step-strictly
                             v-model="setMaxReserveTimeForm.days"/> 天
            <el-input-number size="small" min="-0.1" max="23.1" :step="1" step-strictly
                             v-model="setMaxReserveTimeForm.hours"/> 时
            <el-input-number size="small" min="-0.1" max="59.1" :step="1" step-strictly
                             v-model="setMaxReserveTimeForm.minutes"/> 分
            <el-input-number size="small" min="-0.1" max="59.1" :step="1" step-strictly
                             v-model="setMaxReserveTimeForm.seconds"/> 秒
          </el-col>
          <el-col :span="2">
            <el-button type="primary" icon="el-icon-edit" @click="submitSetMaxReserveTimeForm()"> 更新 </el-button>
          </el-col>

        </el-row>
      </el-form>

      <el-divider/>

      <el-form ref="setMaxBorrowTimeForm" :model="setMaxBorrowTimeForm" class="box-form">
        <el-row>
          <el-col :span="4">
            <span class="span-title">设置最长借阅时间</span>
            <el-divider direction="vertical"/>
          </el-col>
          <el-col :span="3">
            <el-select @change="handleMaxBorrowTimeChange" v-model="setMaxBorrowTimeForm.select" placeholder="请选择">
              <el-option label="教师" value="Teacher"/>
              <el-option label="本科生" value="Undergraduate"/>
              <el-option label="研究生" value="Postgraduate"/>
            </el-select>
          </el-col>
          <el-col :span="15">
            <el-input-number style="margin-top: 3px" size="small" min="-0.1" max="365.1" :step="1" step-strictly
                             v-model="setMaxBorrowTimeForm.days"/> 天
            <el-input-number size="small" min="-0.1" max="23.1" :step="1" step-strictly
                             v-model="setMaxBorrowTimeForm.hours"/> 时
            <el-input-number size="small" min="-0.1" max="59.1" :step="1" step-strictly
                             v-model="setMaxBorrowTimeForm.minutes"/> 分
            <el-input-number size="small" min="-0.1" max="59.1" :step="1" step-strictly
                             v-model="setMaxBorrowTimeForm.seconds"/> 秒
          </el-col>
          <el-col :span="2">
            <el-button type="primary" icon="el-icon-edit" @click="submitSetMaxBorrowTimeForm()"> 更新 </el-button>
          </el-col>
        </el-row>
      </el-form>

      <el-divider/>

      <div style="height: 80px">
        <el-button type="primary" @click="sendEmail">批量发送邮件提醒</el-button>
      </div>

    </el-main>

  </el-container>
</template>

<script>
import axios from 'axios'

export default {
  name: 'systemSettings',
  data() {
    const validatePassword = (rule, value, callback) => {
      let passType = /^[a-zA-Z0-9-_]{6,32}$/.test(value)
      let allNum = /^[0-9]{6,32}$/.test(value)
      let allLetter = /^[a-zA-Z]{6,32}$/.test(value)
      let allSpecial = /^[-_]{6,32}$/.test(value)
      if (passType && !allNum && !allLetter && !allSpecial) {
        if (value.indexOf(this.registerForm.username) >= 0) {
          callback(new Error('密码不得包含用户名'))
        } else {
          callback()
        }
      } else {
        callback(new Error('至少需包含字母、数字、特殊字符（-_）中的两种'))
      }
    }

    const validateCheckPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }

    const validateEmail = (rule, value, callback) => {
      if (!/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(value)) {
        callback(new Error('邮箱格式错误'))
      } else {
        callback()
      }
    }
    return {
      registerForm: {
        username: '',
        password: '',
        checkPassword: ''
      },
      rules: {
        username: [{required: true, message: '请输入管理员名称', trigger: 'blur'}],
        password: [{required: true, message: '请输入密码', trigger: 'blur'}, {
          validator: validatePassword,
          trigger: 'blur'
        }, {min: 6, max: 32, message: '密码长度在6-32个字符'}],
        checkPassword: [{required: true, message: '请重新输入密码', trigger: 'blur'}, {
          validator: validateCheckPassword,
          trigger: 'blur'
        }],
        email: [{required: true, message: '请输入邮箱', trigger: 'blur'}, {validator: validateEmail, trigger: 'blur'}]
      },
      authority: localStorage.getItem('authority') || ['null'],
      setMaxCopiesForm: {
        select: '',
        input: ''
      },
      setMaxReserveTimeForm: {
        select: '',
        days: '',
        hours: '',
        minutes: '',
        seconds: ''
      },
      setMaxBorrowTimeForm: {
        select: '',
        days: '',
        hours: '',
        minutes: '',
        seconds: ''
      },
    }
  },

  methods: {
    handleMaxCopiesChange() {
      let _this = this
      axios.get('http://139.196.174.46:8081/getMaxCopies/' + _this.setMaxCopiesForm.select).then(function (resp) {
        _this.setMaxCopiesForm.input = resp.data
      })
    },
    handleMaxReserveTimeChange() {
      let _this = this
      axios.get('http://139.196.174.46:8081/getMaxReserveTime/' + _this.setMaxReserveTimeForm.select).then(function (resp) {
        let secs = resp.data
        _this.setMaxReserveTimeForm.days = Math.floor(secs / 86400)
        _this.setMaxReserveTimeForm.hours = Math.floor(secs / 3600) - Math.floor(secs / 86400) * 24
        _this.setMaxReserveTimeForm.minutes = Math.floor(secs / 60) - Math.floor(secs / 3600) * 60
        _this.setMaxReserveTimeForm.seconds = Math.floor(secs % 60)
      })
    },
    handleMaxBorrowTimeChange() {
      let _this = this
      axios.get('http://139.196.174.46:8081/getMaxBorrowTime/' + _this.setMaxBorrowTimeForm.select).then(function (resp) {
        let secs = resp.data
        _this.setMaxBorrowTimeForm.days = Math.floor(secs / 86400)
        _this.setMaxBorrowTimeForm.hours = Math.floor(secs / 3600) - Math.floor(secs / 86400) * 24
        _this.setMaxBorrowTimeForm.minutes = Math.floor(secs / 60) - Math.floor(secs / 3600) * 60
        _this.setMaxBorrowTimeForm.seconds = Math.floor(secs % 60)
      })
    },

    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          axios.post('http://139.196.174.46:8081/adminRegister', {
              username: this.registerForm.username,
              password: this.registerForm.password,
              authorities: ['Librarian'],
              email: this.registerForm.email
            }
          )
            .then(resp => {
              if (resp.status === 200) {
                this.$message.success('successfully added')
                this.resetForm(this.registerForm)
              } else {
                this.$message.error('add error')
              }
            })
            .catch(error => {
              this.$message.error(error.response.data.message)
            })
        } else {
          this.$message.error('error submit')
          return false
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
    },
    submitSetMaxCopiesForm() {
      if (/^[0-9]+$/.test(this.setMaxCopiesForm.input) && this.setMaxCopiesForm.select !== '') {
        let formData = new FormData()
        formData.append('maxCopies', this.setMaxCopiesForm.input)
        formData.append('authority', this.setMaxCopiesForm.select)
        axios({
          method: 'post',
          url: 'http://139.196.174.46:8081/setMaxCopies',
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          withCredentials: true,
          data: formData
        })
          .then(resp => {
            if (resp.status === 200) {
              this.$message.success('updated successfully')
            } else {
              this.$message.error('update error')
            }
          })
          .catch(error => {
            this.$message.error(error.response.data.message)
          })
      } else {
        this.$message.error('请选择需要设置最大借阅本数的读者身份')
      }
    },
    submitSetMaxReserveTimeForm() {
      if (this.setMaxReserveTimeForm.select !== '') {
        let secondsInAll = this.setMaxReserveTimeForm.days * 86400 + this.setMaxReserveTimeForm.hours * 3600 + this.setMaxReserveTimeForm.minutes * 60 + this.setMaxReserveTimeForm.seconds * 1
        let formData = new FormData()
        formData.append('maxReserveTime', Number(secondsInAll))
        formData.append('authority', this.setMaxReserveTimeForm.select)
        axios({
          method: 'post',
          url: 'http://139.196.174.46:8081/setMaxReserveTime',
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          withCredentials: true,
          data: formData
        })
          .then(resp => {
            if (resp.status === 200) {
              this.$message.success('updated successfully')
            } else {
              this.$message.error('update error')
            }
          })
          .catch(error => {
            this.$message.error(error.response.data.message)
          })
      } else {
        this.$message.error('请选择需要设置最大预约时间的读者身份')
      }
    },
    submitSetMaxBorrowTimeForm() {
      if (this.setMaxBorrowTimeForm.select !== '') {
        let secondsInAll = this.setMaxBorrowTimeForm.days * 86400 + this.setMaxBorrowTimeForm.hours * 3600 + this.setMaxBorrowTimeForm.minutes * 60 + this.setMaxBorrowTimeForm.seconds * 1
        let formData = new FormData()
        formData.append('maxBorrowTime', Number(secondsInAll))
        formData.append('authority', this.setMaxBorrowTimeForm.select)
        axios({
          method: 'post',
          url: 'http://139.196.174.46:8081/setMaxBorrowTime',
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          withCredentials: true,
          data: formData
        })
          .then(resp => {
            if (resp.status === 200) {
              this.$message.success('updated successfully')
            } else {
              this.$message.error('update error')
            }
          })
          .catch(error => {
            this.$message.error(error.response.data.message)
          })
      } else {
        this.$message.error('请选择需要设置最大借阅时间的读者身份')
      }
    },
    sendEmail() {
      let _this = this
      this.$confirm('此操作将向所有存在违规行为的用户发送提醒邮件，是否确认？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        _this.$message.success('sent successfully')
        axios.get('http://139.196.174.46:8081/sendAlert').then(function (resp) {

        })
          .catch(error => {
            this.$message.error(error.response.data.message)
          })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '邮件未发送'
        })
      })
    }
  },
  //权限拦截
  created() {
    if (this.authority.indexOf('Admin') === -1) {
      this.$message.error('You have no authority to visit this page')
      this.$router.replace('/')
    }
  }
}
</script>

<style scoped>

.box-form {
  margin-top: 30px;
  margin-bottom: 30px;
}

.box-input {
  margin-left: 5px;
  margin-right: 5px;
}

.span-title {
  font-size: 16px;
  font-weight: bold;
  color: #3f475a;
}


</style>
