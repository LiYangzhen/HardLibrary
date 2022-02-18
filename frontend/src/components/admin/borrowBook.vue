<template>
  <el-container class="utilities-page-el-container">
    <el-main class="utilities-page-el-main">
      <el-container style="margin-bottom: 20px; margin-left: 10px">
        <h2 style="text-align: left">借阅登记</h2>
      </el-container>

      <el-form :model="dynamicValidateForm" :ref="dynamicValidateForm" label-width="100px" class="demo-dynamic"
               :rules="rules">
        <el-form-item prop="username" label="用户名">
          <el-input v-model="dynamicValidateForm.username"/>
        </el-form-item>

        <el-form-item
          v-for="(copiesISBNs, index) in dynamicValidateForm.copiesISBNs"
          :label="`副本${(index + 1)}`"
          :key="copiesISBNs.key"
          :prop="'copiesISBNs.' + index + '.value'"
          :rules="rules.value"
        >
          <el-col span="18">
            <el-input v-model="copiesISBNs.value" @blur="showDetails(copiesISBNs.value,index)"/>
          </el-col>
          <el-col span="6">
            <el-button @click.prevent="removeCopy(copiesISBNs)" type="danger" class="utilities-page-delete-button">删除
            </el-button>
          </el-col>
          <div v-if="ifShowDetails[index]">
            <el-image style="width: 50%; height: 50%; margin-top: 10px; margin-bottom: 5px; border-radius: 20px"
                      :src="'data:image/jpeg;base64,'+ copyDetails[index].image"
                      :preview-src-list="['data:image/jpeg;base64,'+ copyDetails[index].image]" :fit="fit">
              <div slot="placeholder" class="image-slot">加载中<span class="dot">...</span></div>
            </el-image>
            <div style="color: #666">
              {{ copyDetails[index].name }}&nbsp;&nbsp;&nbsp;&nbsp;{{ copyDetails[index].author }}
            </div>
          </div>
        </el-form-item>

        <el-form-item class="utilities-page-submit-buttons">
          <el-button type="primary" @click="submitForm(dynamicValidateForm)">提交</el-button>
          <el-button @click="addCopy()">添加一个借出副本</el-button>
          <el-button @click="resetForm(dynamicValidateForm)">重置</el-button>
        </el-form-item>
      </el-form>
    </el-main>

  </el-container>
</template>

<script>
import axios from 'axios'

export default {
  name: 'borrowBook',
  data() {
    //验证用户名
    const validateUsername = (rule, value, callback) => {
      if (!/^[0-9]{11}$/.test(value))
        callback(new Error('请输入11位学号'))
      else
        callback()
    }
    //验证副本标识是否合法，以数字和-组成，且最后三位是-xxx
    const validateCopy = (rule, value, callback) => {
      if (!/^[0-9]+(-[0-9]+)*-[0-9]{3}$/.test(value))
        callback(new Error('请输入合法的副本标识'))
      else
        callback()
    }
    return {
      dynamicValidateForm: {
        copiesISBNs: [{
          key: 0,
          value: ''
        }],
        username: ''
      },
      rules: {
        username: [{required: true, message: '请输入学号/工号', trigger: 'blur'}, {
          validator: validateUsername,
          trigger: 'blur'
        },
          {min: 11, max: 11, message: '请输入11位学号'}],
        value: [{required: true, message: '请输入副本标识', trigger: 'blur'}, {
          validator: validateCopy,
          trigger: 'blur'
        }]
      },
      authority: localStorage.getItem('authority') || ['null'],
      ifShowDetails: [],
      copyDetails: []
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let copiesIsbn = []
          let noSameIsbn = true;
          //将副本标识手动加入一个数组，便于传递给后端
          for (let i = 0; i < this.dynamicValidateForm.copiesISBNs.length; i++) {
            for (let j = 0; j < i ; j++){
              if (this.dynamicValidateForm.copiesISBNs[i].value === copiesIsbn[j]){
                noSameIsbn = false
              }
            }
            copiesIsbn[i] = this.dynamicValidateForm.copiesISBNs[i].value
          }
          if (noSameIsbn){
            axios.post('http://139.196.174.46:8081/borrowBook', {
                location: localStorage.getItem('librarianLocation'),
                username: this.dynamicValidateForm.username,
                copiesIsbn: copiesIsbn,
                admin: localStorage.getItem('userDetails')
              }
            )
              .then(resp => {
                // 根据后端的返回数据修改
                if (resp.status === 200) {
                  this.$message.success('Successfully borrowed!')
                  this.resetForm(this.dynamicValidateForm)
                } else {
                  this.$message.error('Borrow error')
                }
              })
              .catch(error => {
                this.$message.error(error.response.data.message)
              })
          } else {
            this.$message.error('存在两个相同副本编号，请修改')
          }
        } else {
          this.$message.error('Submit error')
          return false
        }
      })
    },
    //重置表单
    resetForm(formName) {
      this.$refs[formName].resetFields()
      for (let i = 0; i < this.ifShowDetails.length; i++) {
        this.$set(this.ifShowDetails, i, false)
        this.$set(this.copyDetails, i, null)
      }
    },
    //动态增减表单项
    removeCopy(item) {
      let index = this.dynamicValidateForm.copiesISBNs.indexOf(item)
      if (index < this.dynamicValidateForm.copiesISBNs.length - 1) {
        for (let i = index; i < this.dynamicValidateForm.copiesISBNs.length - 1; i++) {
          this.$set(this.ifShowDetails, i, this.ifShowDetails[i + 1])
          this.$set(this.copyDetails, i, this.copyDetails[i + 1])
        }
      }
      this.$set(this.ifShowDetails, this.dynamicValidateForm.copiesISBNs.length - 1, false)
      this.$set(this.copyDetails, this.dynamicValidateForm.copiesISBNs.length - 1, null)
      if (index !== -1)
        this.dynamicValidateForm.copiesISBNs.splice(index, 1)
    },
    addCopy() {
      this.dynamicValidateForm.copiesISBNs.push({
        value: '',
        key: this.dynamicValidateForm.copiesISBNs.length
      })
    },
    showDetails(isbn, index) {
      let _this = this
      axios.get('http://139.196.174.46:8081/findBookByIsbn/' + isbn.substr(0, isbn.length - 4)).then(function (resp) {
        _this.$set(_this.copyDetails, index, resp.data)
      })
        .catch(error => {
          this.$set(this.ifShowDetails, index, false)
          this.$set(_this.copyDetails, index, null)
          this.$message.error('isbn invalid')
        })
      this.$set(this.ifShowDetails, index, true)
    }
  },
  //身份拦截，管理员才能访问这一界面
  created() {
    if (this.authority.indexOf('Librarian') === -1) {
      this.$message.error('您没有访问该页面的权限！')
      this.$router.replace('/')
    }
    this.$set(this.ifShowDetails, 0, false)
  }
}
</script>

<style scoped>

</style>
