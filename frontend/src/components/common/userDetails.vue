<template>
  <el-container style="margin: 0 0 60px">
    <el-main class="out-box-white" style="background-color: #fdfdfd; border-radius: 20px">
      <div style="margin-bottom: 20px; margin-left: 10px">
        <h2 style="text-align: left">个人中心</h2>
      </div>
      <el-row>
        <el-col :span="1">&nbsp;</el-col>
        <el-col :span="17">
          <div style="margin-top: 40px; margin-bottom: 20px">
            <el-row>
              <el-col :span="4">
                <span class="span-title">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;用户名</span>
                <el-divider direction="vertical"/>
              </el-col>
              <el-col :span="2" style="font-family: 'OCR A Extended'; text-align: left">
                &nbsp;{{ this.username }}
              </el-col>
              <el-col :span="18"/>
            </el-row>
          </div>

          <div style="margin-top: 20px; margin-bottom: 20px" v-if="authority.indexOf('Reader') > -1">
            <el-row>
              <el-col :span="4">
                <span class="span-title">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;学校邮箱</span>
                <el-divider direction="vertical"/>
              </el-col>
              <el-col :span="2" style="font-family: 'OCR A Extended'; text-align: left">
                &nbsp;{{ this.username }}@fudan.edu.cn
              </el-col>
              <el-col :span="18"/>
            </el-row>
          </div>

          <div style="margin-top: 20px; margin-bottom: 20px">
            <el-row>
              <el-col :span="4">
                <span class="span-title">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;用户类别</span>
                <el-divider direction="vertical"/>
              </el-col>
              <el-col :span="6" style="font-family: 'OCR A Extended'; text-align: left">
                &nbsp;{{ this.authority }}
              </el-col>
              <el-col :span="14"/>
            </el-row>
          </div>

          <div style="margin-top: 20px; margin-bottom: 20px" v-if="authority.indexOf('Reader') > -1">
            <el-row>
              <el-col :span="4">
                <span class="span-title">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;应缴罚款</span>
                <el-divider direction="vertical"/>
              </el-col>
              <el-col :span="4" style="font-family: 'OCR A Extended'; text-align: left">
                &nbsp;{{ this.fine.toFixed(2) }} 元
              </el-col>
              <el-col :span="6">
                <el-button type="success" icon="el-icon-wallet" style="margin-top: -7px; margin-left: 30px" v-on:click="payFine()"> 缴纳罚款</el-button>
              </el-col>
              <el-col :span="10"/>
            </el-row>
          </div>

          <div style="margin-top: 20px; margin-bottom: 20px" v-if="authority.indexOf('Reader') > -1">
            <el-row>
              <el-col :span="4">
                <span class="span-title">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;信用分值</span>
                <el-divider direction="vertical"/>
              </el-col>
              <el-col :span="4" style="font-family: 'OCR A Extended'; text-align: left">
                <span v-if="this.credit == 100" style="color: #61b033; font-weight: bolder; font-size: 24px">&nbsp;{{ this.credit }} </span>
                <span v-if="this.credit >= 80 && this.credit < 100" style="color: #a1da00; font-weight: bolder; font-size: 24px">&nbsp;{{ this.credit }} </span>
                <span v-if="this.credit >= 50 && this.credit < 80" style="color: #e4e300; font-weight: bolder; font-size: 24px">&nbsp;{{ this.credit }} </span>
                <span v-if="this.credit > 0 && this.credit < 50" style="color: #f49400;; font-weight: bolder; font-size: 24px">&nbsp;{{ this.credit }} </span>
                <span v-if="this.credit == 0" style="color: #ce2d00; font-weight: bolder; font-size: 24px">&nbsp;{{ this.credit }} </span>
                / 100
              </el-col>
              <el-col :span="6">
                <el-button type="primary" icon="el-icon-refresh-right" style="margin-top: -7px; margin-left: 30px" @click="resetCredit"> 信用重置</el-button>
              </el-col>
              <el-col :span="3">
                <el-button type="info" icon="el-icon-question" style="margin-top: -7px" @click="openCreditDetails"> 规则详情</el-button>
              </el-col>
              <el-col :span="7"/>

            </el-row>
          </div>

          <el-form :model="passwordForm" :rules="rules" :ref="passwordForm" size="small" class="box-form">
            <el-row>
              <el-col :span="4">
                <span class="span-title">修改账户密码</span>
                <el-divider direction="vertical"/>
              </el-col>
              <el-col :span="5">
                <el-form-item prop="oldPassword" class="box-input">
                  <el-input type="password" v-model="passwordForm.oldPassword" auto-complete="off" placeholder="旧密码"/>
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item prop="newPassword" class="box-input">
                  <el-input type="password" v-model="passwordForm.newPassword" auto-complete="off" placeholder="新密码"/>
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item prop="newPasswordConfirm" class="box-input">
                  <el-input type="password" v-model="passwordForm.newPasswordConfirm" auto-complete="off"
                            placeholder="确认新密码"/>
                </el-form-item>
              </el-col>
              <el-col :span="3" class="box-input">
                <el-form-item>
                  <el-button type="primary" style="margin-top: -5px" icon="el-icon-edit" v-on:click="submit(passwordForm)">
                    确认修改
                  </el-button>
                </el-form-item>
              </el-col>
              <el-col :span="1"></el-col>
            </el-row>
          </el-form>
        </el-col>

        <el-col :span="6"  v-if="authority.indexOf('Reader') > -1">
          <img src="../../assets/background/background_main1.jpg" style="width: 220px; height: 220px; margin-top: 30px; object-fit: cover; border-radius: 50%" alt=""/>
          <div style="margin-top: 10px; margin-left: 0px">
            <span style="color: #bbb; font-size: 12px; font-family: 'OCR A Extended'">HardLibrary Premium Member</span>
          </div>
        </el-col>
      </el-row>

      <el-divider v-if="authority.indexOf('Reader') > -1"/>

      <el-tabs type="border-card" value="first" v-if="authority.indexOf('Reader') > -1" style="border-radius: 5px">
        <el-tab-pane label="当前借阅记录" name="first">
          <el-table :data="this.currentRecords" stripe style="width: 95%; margin: 20px auto; border: 1px solid #ccc; border-radius: 10px;">
            <el-table-column label="副本标识" prop="copy.isbn" sortable width="200" fixed="left"/>
            <el-table-column label="书名" prop="name" sortable width="200" fixed="left"/>
            <el-table-column label="作者" prop="author" sortable width="150"/>
            <el-table-column label="预约时间" prop="record.reserveTime" sortable width="150"/>
            <el-table-column label="借阅时间" prop="record.borrowTime" sortable width="150"/>
            <el-table-column label="借阅地点" prop="record.borrow_location" width="100"/>
            <el-table-column label="借阅操作者" prop="record.borrow_admin" width="100"/>
            <el-table-column label="预约/借阅到期" prop="expiration" sortable width="150" fixed="right"/>
            <el-table-column label="当前进度" sortable fixed="right" width="100">
              <template slot-scope="props">
                <div v-if="props.row.record.status === '已过期'">
                  预约过期
                </div>
                <div v-else>
                  {{ props.row.record.status }}
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="历史借阅记录" name="second">
          <el-table :data="this.borrowRecords" max-height="400" stripe style="width: 95%; margin: 20px auto; border: 1px solid #ccc; border-radius: 10px;">
            <el-table-column label="副本标识" prop="copy.isbn" sortable width="200" fixed="left"/>
            <el-table-column label="书名" prop="name" sortable width="200" fixed="left"/>
            <el-table-column label="作者" prop="author" sortable width="150"/>
            <el-table-column label="预约时间" prop="record.reserveTime" sortable width="150"/>
            <el-table-column label="借阅时间" prop="record.borrowTime" sortable width="150"/>
            <el-table-column label="预约/借阅到期" prop="expiration" sortable width="150"/>
            <el-table-column label="借阅地点" prop="record.borrow_location" width="100"/>
            <el-table-column label="借阅操作者" prop="record.borrow_admin" width="100"/>
            <el-table-column label="归还时间" prop="record.returnTime" sortable width="150"/>
            <el-table-column label="归还地点" prop="record.return_location" width="100"/>
            <el-table-column label="归还操作者" prop="record.return_admin" width="100"/>
            <el-table-column label="应缴罚款" prop="record.fine" sortable width="100"/>
            <el-table-column label="当前进度" sortable fixed="right" width="100">
              <template slot-scope="props">
                <div v-if="props.row.record.status === '已过期'">
                  预约过期
                </div>
                <div v-else>
                  {{ props.row.record.status }}
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="信用流水记录" name="third">
          <el-table :data="this.creditRecords" max-height="400" stripe style="width: 95%; margin: 20px auto;border: 1px solid #ccc; border-radius: 10px;">
            <el-table-column label="副本标识" prop="copy.isbn" sortable width="200" fixed="left"/>
            <el-table-column label="书名" prop="name" sortable width="200" fixed="left"/>
            <el-table-column label="作者" prop="author" sortable width="150"/>
            <el-table-column label="预约时间" prop="record.reserveTime" sortable width="150"/>
            <el-table-column label="借阅时间" prop="record.borrowTime" sortable width="150"/>
            <el-table-column label="预约/借阅到期" prop="expiration" sortable width="150"/>
            <el-table-column label="归还时间" prop="record.returnTime" sortable width="150"/>
            <el-table-column label="变动原因" sortable fixed="right" width="100">
              <template slot-scope="props">
                <div v-if="props.row.record.status === '已过期'">
                  预约过期
                </div>
                <div v-else>
                  {{ props.row.record.status }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="变动分值" prop="record.credit" sortable width="100" fixed="right"/>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="信用重置记录" name="fourth">
        <el-table :data="this.resetRecords" max-height="300" stripe
                  style="width: 95%; margin: 20px auto;border: 1px solid #ccc; border-radius: 10px;">
          <el-table-column label="发起时间" prop="time" sortable/>
          <el-table-column label="重置是否成功" prop="pass" sortable>
            <template slot-scope="props">
              <div v-if="props.row.pass === true">
                重置成功
              </div>
              <div v-else>
                重置失败
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      </el-tabs>
      <div style="height: 50px"/>

    </el-main>

  </el-container>
</template>

<script>
import axios from 'axios'

export default {
  name: 'userDetails',

  data() {
    //验证密码格式
    const validatePassword = (rule, value, callback) => {
      let passType = /^[a-zA-Z0-9-_]{6,32}$/.test(value)
      let allNum = /^[0-9]{6,32}$/.test(value)
      let allLetter = /^[a-zA-Z]{6,32}$/.test(value)
      let allSpecial = /^[-_]{6,32}$/.test(value)
      if (passType && !allNum && !allLetter && !allSpecial) {
        if (value.indexOf(localStorage.getItem('userDetails')) >= 0) {
          callback(new Error('密码不得包含用户名'))
        } else {
          callback()
        }
      } else {
        callback(new Error('至少需包含字母、数字、特殊字符（-_）中的两种'))
      }
    }
    //验证两次密码是否一致
    const validateCheckPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }

    return {
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        newPasswordConfirm: ''
      },
      rules: {
        oldPassword: [{required: true, message: '', trigger: 'blur'}],
        newPassword: [{required: true, message: '', trigger: 'blur'}, {validator: validatePassword, trigger: 'blur'},
          {min: 6, max: 32, message: '密码长度在6-32个字符'}],
        newPasswordConfirm: [{required: true, message: '', trigger: 'blur'}, {
          validator: validateCheckPassword,
          trigger: 'blur'
        }]
      },
      borrowRecords: [
        {
          name: '',
          author: '',
          expiration: '',
          copy: {
            borrower: '',
            isbn: '',
            location: '',
            status: '',
            time: '',
            time_limit: ''
          },
          record: {
            borrowTime: '',
            borrow_admin: '',
            borrow_location: '',
            borrower: '',
            id: '',
            isbn: '',
            reserveTime: '',
            returnTime: '',
            return_admin: '',
            return_location: '',
            status: '',
            credit: ''
          }
        }
      ],
      currentRecords: [
        {
          name: '',
          author: '',
          expiration: '',
          copy: {
            borrower: '',
            isbn: '',
            location: '',
            status: '',
            time: '',
            time_limit: ''
          },
          record: {
            borrowTime: '',
            borrow_admin: '',
            borrow_location: '',
            borrower: '',
            id: '',
            isbn: '',
            reserveTime: '',
            returnTime: '',
            return_admin: '',
            return_location: '',
            status: '',
            credit: ''
          }
        }
      ],
      creditRecords: [
        {
          name: '',
          author: '',
          expiration: '',
          copy: {
            borrower: '',
            isbn: '',
            location: '',
            status: '',
            time: '',
            time_limit: ''
          },
          record: {
            borrowTime: '',
            borrow_admin: '',
            borrow_location: '',
            borrower: '',
            id: '',
            isbn: '',
            reserveTime: '',
            returnTime: '',
            return_admin: '',
            return_location: '',
            status: '',
            credit: ''
          }
        }
      ],
      username: localStorage.getItem('userDetails') || ['null'],
      authority: localStorage.getItem('authority') || ['null'],
      fine: 0,
      credit: 100,
      resetRecords: [{
        id: '',
        username: '',
        time: '',
        pass: false
      }],
    }
  },
  methods: {
    openCreditDetails() {
      this.$notify.info({
        title: '信用分规则介绍',
        message: '1.信用分上限为100分，最低分为0分。2.每次预约过期未取扣10分，逾期还书扣20分，图书损坏扣30分，图书遗失扣40分。3.信用分低于50分将无法预约图书，只能现场取书；信用分达到0无法再借阅图书。4.若信用分低于100分，可提交信用重置申请，若1）已缴纳所有罚款 2）已归还所有过期书籍 则重置信用分为100。',
        duration: 10000,
        offset: 150,
      })
    },
    resetCredit() {
      if (this.credit < 100) {
        let _this = this
        axios.get('http://139.196.174.46:8081/resetCredit/' + localStorage.getItem('userDetails')).then(function (resp) {
          if (resp.status === 200) {
            _this.credit = 100
            _this.$message.success('信用分已重置为100，下次注意点！')
          } else {
            this.$message.error('error')
          }
        })
          .catch(error => {
            this.$message.error(error.response.data.message)
          })
      } else {
        this.$message.warning('信用分已满，无需重置！')
      }
    },
    //清除状态筛选
    filterTag(value, row) {
      return row.status === value
    },
    //各状态的外观
    getStatus: function (status) {
      switch (status) {
        case '可借阅':
          return 'success'
        case '已借阅':
          return 'warning'
        case '已遗失':
          return 'danger'
        case '已预约':
          return 'info'
      }
    },
    submit(form) {
      this.$refs[form].validate(valid => {
        if (valid) {
          let formData = new FormData()
          //向后端传递三个参数
          formData.append('username', localStorage.getItem('userDetails'))
          formData.append('oldPassword', this.passwordForm.oldPassword)
          formData.append('newPassword', this.passwordForm.newPassword)
          axios({
            method: 'post',
            url: 'http://139.196.174.46:8081/changePassword',
            headers: {
              'Content-Type': 'multipart/form-data'
            },
            withCredentials: true,
            data: formData
          })
            .then(resp => {
              if (resp.status === 200) {
                this.$message.success('Password changed successfully')
                this.resetForm(this.passwordForm)
              } else {
                this.$message.error('error')
              }
            })
            .catch(error => {
              this.$message.error(error.response.data.message)
            })
        } else {
          this.$message.error('wrong submit')
        }
      })
    },
    //重置表单
    resetForm(formName) {
      this.$refs[formName].resetFields()
    },
    payFine() {
      if (this.fine > 0) {
        let _this = this
        axios.post('/api/payment', {
            "invoke_id": "se2021_5",
            "uid": localStorage.getItem('userDetails'),
            "amount": _this.fine.toFixed(2)
          }
        )
          .then(resp => {
            // 根据后端的返回数据修改
            if (resp.status === 200) {
              this.$message.success('已缴纳罚款')
              axios.get('http://139.196.174.46:8081/payFine/' + localStorage.getItem('userDetails')).then(function (resp) {

              })
              _this.fine = 0
            } else {
              this.$message.error('error')
            }
          })
          .catch(error => {
            this.$message.error(error.response.data.msg)
          })
      } else {
        this.$message.warning('无应缴罚款')
      }

    }
  },

  //权限拦截
  created() {
    if (this.authority.indexOf('Reader') === -1 && this.authority.indexOf('Librarian') === -1) {
      this.$message.error('You have no authority to visit this page')
      this.$router.replace('/')
    } else {
      let _this = this

      if (this.authority.indexOf('Reader') > -1) {
        axios.get('http://139.196.174.46:8081/getUserRecords/' + localStorage.getItem('userDetails')).then(function (resp) {
          if (resp.data.length !== 0) {
            _this.borrowRecords = resp.data
            let j = 0, k = 0
            for (let i = 0; i < resp.data.length; i++) {
              if (resp.data[i].record.status === '进行中' || resp.data[i].record.status === '已逾期') {
                _this.$set(_this.currentRecords, j, resp.data[i])
                j++
              }
              if (resp.data[i].record.credit !== null) {
                _this.$set(_this.creditRecords, k, resp.data[i])
                // _this.creditRecords[k].record.status = "该图书" + resp.data[i].record.status
                _this.creditRecords[k].record.credit = "-" + resp.data[i].record.credit + "分"
                k++
              }
            }
          }
          //没有借阅记录，给出相应提示
          else {
            _this.$message.warning('no records to show')
          }
        })
          .catch(error => {
            this.$message.error(error.response.data.message)
          })

        axios.get('http://139.196.174.46:8081/getFine/' + localStorage.getItem('userDetails')).then(function (resp) {
          _this.fine = resp.data
        })
          .catch(error => {
            this.$message.error(error.response.data.message)
          })

        axios.get('http://139.196.174.46:8081/getCredit/' + localStorage.getItem('userDetails')).then(function (resp) {
          _this.credit = resp.data
        })
          .catch(error => {
            this.$message.error(error.response.data.message)
          })
        axios.get('http://139.196.174.46:8081/getResetRecords/' + localStorage.getItem('userDetails')).then(function (resp) {
          if (resp.data.length !== 0) {
            _this.resetRecords = resp.data
          } else {
            _this.$message.warning('no records to show')
          }
        })
          .catch(error => {
            this.$message.error(error.response.data.message)
          })
      }

    }
  }
}
</script>

<style scoped>
.box-form {
  margin-top: 20px;
  margin-bottom: 20px;
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
