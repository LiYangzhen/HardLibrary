<template>
  <el-container style="margin: 0 0 60px;">

    <el-header
      style="height: 80px; padding-top: 10px; padding-bottom: 10px; background-color: rgb(56,141,220); border-radius: 20px 20px 0 0;">
      <el-form ref="form" :model="form">
        <el-input placeholder="请输入内容" v-model="form.input" class="input-with-select">
          <el-select v-model="form.select" slot="prepend" placeholder="请选择">
            <el-option label="按用户搜索" value="username"/>
            <el-option label="按副本搜索" value="isbn"/>
          </el-select>
          <el-button slot="append" icon="el-icon-search" @click="submitForm(form)">搜索</el-button>
        </el-input>
      </el-form>
    </el-header>

    <el-main v-if="!userHistory && !copyHistory" class="out-box" style="background-color: #fdfdfd;">
      <div v-if="!userHistory" style="margin: 100px; font-size: 1.5em; color: #577485"><b>【记录查询】&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;请在上方横框中输入内容后，点击
        <i class="el-icon-search"/> 搜索</b></div>
    </el-main>

    <el-main v-if="userHistory" class="out-box" style="background-color: #fdfdfd;">
      <el-tabs type="border-card" value="first" style="border-radius: 5px; margin-top: 10px; margin-bottom: 10px">
        <el-tab-pane label="当前借阅记录" name="first">
          <h3 style="text-align: left">用户 {{ this.username }} 的当前借阅记录</h3>
          <el-table :data="this.currentRecords" stripe
                    style="width: 95%; margin: 20px auto; border: 1px solid #ccc; border-radius: 10px;">
            <el-table-column label="副本标识" prop="copy.isbn" sortable fixed="left"/>
            <el-table-column label="书名" prop="name" sortable fixed="left"/>
            <el-table-column label="作者" prop="author" sortable/>
            <el-table-column label="预约时间" prop="record.reserveTime" sortable/>
            <el-table-column label="借阅时间" prop="record.borrowTime" sortable/>
            <el-table-column label="借阅操作者" prop="record.borrow_admin"/>
            <el-table-column label="借阅地点" prop="record.borrow_location"/>
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
          <h3 style="text-align: left">用户 {{ this.username }} 的历史借阅记录</h3>
          <el-table :data="this.borrowRecords" max-height="400" stripe
                    style="width: 95%; margin: 20px auto; border: 1px solid #ccc; border-radius: 10px;">
            <el-table-column label="副本标识" prop="copy.isbn" sortable width="200" fixed="left"/>
            <el-table-column label="书名" prop="name" sortable width="200" fixed="left"/>
            <el-table-column label="作者" prop="author" sortable width="150"/>
            <el-table-column label="预约时间" prop="record.reserveTime" sortable width="150"/>
            <el-table-column label="借阅时间" prop="record.borrowTime" sortable width="150"/>
            <el-table-column label="预约/借阅到期" prop="expiration" sortable width="150"/>
            <el-table-column label="借阅操作者" prop="record.borrow_admin" width="100"/>
            <el-table-column label="借阅地点" prop="record.borrow_location" width="100"/>
            <el-table-column label="归还时间" prop="record.returnTime" sortable width="150"/>
            <el-table-column label="归还操作者" prop="record.return_admin" width="100"/>
            <el-table-column label="归还地点" prop="record.return_location" width="100"/>
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
          <h3 style="text-align: left">用户 {{ this.username }} 的信用流水记录</h3>
          <el-table :data="this.creditRecords" max-height="400" stripe
                    style="width: 95%; margin: 20px auto;border: 1px solid #ccc; border-radius: 10px;">
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
          <h3 style="text-align: left">用户 {{ this.username }} 的信用重置记录</h3>
          <el-table :data="this.resetRecords" max-height="300" stripe
                    style="width: 95%; margin: 20px auto;border: 1px solid #ccc; border-radius: 10px;">
            <el-table-column label="用户名" prop="username" sortable fixed="left"/>
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
    </el-main>


    <el-main v-if="copyHistory" class="out-box" style="background-color: #fdfdfd;">
      <h3 style="text-align: left">图书 {{ this.copyRecords[0].copy.isbn }} {{ this.copyRecords[0].name }}
        {{ this.copyRecords[0].author }}</h3>
      <h3 style="text-align: left">历史借阅记录</h3>
      <el-table :data="this.copyRecords" max-height="400" stripe
                style="width: 95%; margin: 20px auto 30px;border: 1px solid #ccc; border-radius: 10px;">
        <el-table-column label="借阅者" prop="record.borrower" sortable width="150" fixed="left"/>
        <el-table-column label="预约时间" prop="record.reserveTime" sortable width="150"/>
        <el-table-column label="借阅时间" prop="record.borrowTime" sortable width="150"/>
        <el-table-column label="预约/借阅到期" prop="expiration" sortable width="150"/>
        <el-table-column label="借阅操作者" prop="record.borrow_admin" width="100"/>
        <el-table-column label="借阅地点" prop="record.borrow_location" width="100"/>
        <el-table-column label="归还时间" prop="record.returnTime" sortable width="150"/>
        <el-table-column label="归还操作者" prop="record.return_admin" width="100"/>
        <el-table-column label="归还地点" prop="record.return_location" width="100"/>
        <el-table-column label="当前进度" prop="record.status" sortable fixed="right" width="100"/>
      </el-table>
    </el-main>

  </el-container>
</template>

<script>
import axios from 'axios'

export default {
  name: 'historyManagement',

  data() {
    return {
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
            status: ''
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
            status: ''
          }
        }
      ],
      copyRecords: [{
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
          status: ''
        }
      }],
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
      resetRecords: [{
        id: '',
        username: '',
        time: '',
        pass: false
      }],
      authority: localStorage.getItem('authority') || ['null'],
      form: {
        input: '',
        select: 'username'
      },
      userHistory: false,
      copyHistory: false,
      username: ''
    }
  },
  methods: {
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
    submitForm() {
      let _this = this
      //根据不同搜索内容，调用不同接口
      if (this.form.select === 'username') {
        axios.get('http://139.196.174.46:8081/getUserRecords/' + _this.form.input).then(function (resp) {
          if (resp.data.length !== 0) {
            _this.userHistory = true
            _this.copyHistory = false
            _this.username = _this.form.input
            _this.borrowRecords = resp.data
            let j = 0, k = 0
            for (let i = 0; i < resp.data.length; i++) {
              if (resp.data[i].record.status === '进行中' || resp.data[i].record.status === '已逾期') {
                _this.currentRecords[j] = resp.data[i]
                j++
              }
              if (resp.data[i].record.credit !== null) {
                _this.creditRecords[k] = resp.data[i]
                // _this.creditRecords[k].record.status = "该图书" + resp.data[i].record.status
                _this.creditRecords[k].record.credit = "-" + resp.data[i].record.credit + "分"
                k++
              }
            }
          }
          //没有借阅记录，给出相应提示
          else {
            _this.$message.warning('no records to show')
            _this.userHistory = false
            _this.copyHistory = false
            _this.username = null
          }
        })
        axios.get('http://139.196.174.46:8081/getResetRecords/' + _this.form.input).then(function (resp) {
          if (resp.data.length !== 0) {
            _this.resetRecords = resp.data
          } else {
            _this.$message.warning('no records to show')
            _this.userHistory = false
            _this.copyHistory = false
            _this.username = null
          }
        })
          .catch(error => {
            this.$message.error(error.response.data.message)
            _this.userHistory = false
            _this.copyHistory = false
            _this.username = null
          })
      } else if (this.form.select === 'isbn') {
        axios.get('http://139.196.174.46:8081/getCopyHistory/' + _this.form.input).then(function (resp) {
          console.log(resp.data)
          if (resp.data.length !== 0) {
            _this.copyRecords = resp.data
            _this.userHistory = false
            _this.copyHistory = true
            _this.username = null
          }
          //没有借阅记录，给出相应提示
          else {
            _this.$message.warning('no records to show')
            _this.userHistory = false
            _this.copyHistory = false
            _this.username = null
          }
        })
          .catch(error => {
            this.$message.error(error.response.data.message)
            _this.userHistory = false
            _this.copyHistory = false
            _this.username = null
          })
      }
    }
  },
  //权限拦截
  created() {
    if (this.authority.indexOf('Librarian') === -1) {
      this.$message.error('You have no authority to visit this page')
      this.$router.replace('/')
    }
  }
}
</script>

<style scoped>


</style>
