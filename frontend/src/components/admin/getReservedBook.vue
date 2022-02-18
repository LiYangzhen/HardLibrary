<template>
  <el-container class="utilities-page-el-container">
    <el-main class="utilities-page-el-main">
      <el-container style="margin-bottom: 20px; margin-left: 10px">
        <h2 style="text-align: left">领取已预约图书</h2>
      </el-container>

      <el-form :model="form" ref="form" label-width="100px" :rules="rules" :inline="true">
        <el-form-item prop="username" label="用户名">
          <el-input v-model="form.username"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('form')">查询</el-button>
        </el-form-item>
      </el-form>

      <el-form v-if="haveReservation" :model="bookForm" ref="bookForm">
        <el-table ref="multipleTable"
          :data="reservedBooks"
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange">
          <el-table-column type="selection" :selectable="handleDisable"/>
          <el-table-column prop="copy.isbn" label="ISBN"/>
          <el-table-column prop="name" label="书名"/>
          <el-table-column prop="author" label="作者"/>
          <el-table-column prop="copy.location" label="地址"/>
        </el-table>
        <el-form-item v-for="i in reservedBooks.length" :key="i"/>
        <el-form-item :model="bookForm" ref="bookForm" label-width="100px"
                      class="utilities-page-submit-buttons" style="margin-top: 20px;">
          <el-button type="primary" @click="submitBookForm('bookForm')">确认取走书籍</el-button>
        </el-form-item>
      </el-form>
    </el-main>

  </el-container>
</template>

<script>
import axios from 'axios'

export default {
  name: 'getReservedBook',
  data() {
    return {
      form: {
        username: ''
      },
      rules: {
        username: {required: true, trigger: blur(), message: '请输入用户名'}
      },
      bookForm: {
        checked1: false
      },
      min: 1,
      max: 5,
      books: [false, false, false, false, false],
      haveReservation: false,
      username: '',
      reservedBooks: [
        {
          name: '',
          author: '',
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
      librarianLocation: localStorage.getItem('librarianLocation'),
      authority: localStorage.getItem('authority') || ['null']
    }
  },

  methods: {
    //选中的图书改变，更新相应变量
    handleSelectionChange(val){
      this.books = val
    },
    //提交用户名，查询预约信息
    submitForm(formName) {
      this.haveReservation = false
      let _this = this
      this.$refs[formName].validate((valid) => {
        if (valid) {
          axios.get('http://139.196.174.46:8081/getReservedBook/' + this.form.username).then(function (resp) {
            if (resp.data.length !== 0) {
              _this.username = _this.form.username
              _this.reservedBooks = resp.data
              _this.haveReservation = true
              console.log(resp.data)
            }
            //若没有预约，给出提示
            else {
              _this.$message.warning('Nothing reserved!')
            }
          })
            .catch(error => {
              this.$message.error(error.response.data.message)
            })
        } else {
          this.$message.error('Submit error')
          return false
        }
      })
    },
    //领取预约书籍
    submitBookForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (!this.books[0] && !this.books[1] && !this.books[2] && !this.books[3] && !this.books[4]) {
            this.$message.error('Submit error')
          } else {
            let copiesIsbn = []
            for (let j = 0; j < this.books.length; j++) {
              copiesIsbn[j] = this.books[j].copy.isbn
            }
            axios.post('http://139.196.174.46:8081/takeReservedBook', {
                location: localStorage.getItem('librarianLocation'),
                username: this.username,
                admin: localStorage.getItem('userDetails'),
                copiesIsbn: copiesIsbn
              }
            )
              .then(resp => {
                if (resp.status === 200) {
                  this.$message.success('Successfully taken!')
                  setTimeout(function () {
                    location.reload()
                  }, 2000)
                } else {
                  this.$message.error('Get book error')
                }
              })
              .catch(error => {
                this.$message.error(error.response.data.message)
              })
          }
        } else {
          this.$message.error('Submit error')
          return false
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
    },
    //若预约副本不在当前图书馆，则不能选中
    handleDisable(row, index) {
      if (row.copy.location !== this.librarianLocation)
        return false
      else
        return true
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
