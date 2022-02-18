<template>
  <el-container class="utilities-page-el-container">
    <el-main class="utilities-page-el-main">
      <el-container style="margin-bottom: 20px; margin-left: 10px">
        <h2 style="text-align: left">图书归还</h2>
      </el-container>

      <el-form :model="dynamicValidateForm" :ref="dynamicValidateForm" label-width="100px" class="demo-dynamic"
               :rules="rules">
        <el-form-item
          v-for="(copiesISBNs, index) in dynamicValidateForm.copiesISBNs"
          :label="`副本${(index + 1)}`"
          :key="copiesISBNs.key"
          :prop="'copiesISBNs.' + index + '.value'"
          :rules="rules.value"
        >
          <el-col span="9">
            <el-input v-model="copiesISBNs.value" @blur="showDetails(copiesISBNs.value,index)"/>
          </el-col>
          <el-col span="11">
            <el-radio-group v-model="dynamicValidateForm.radioArray[index]" size="large"
                            style="margin-bottom: 3px; margin-left: 3px">
              <el-radio-button label="损坏"/>
              <el-radio-button label="遗失"/>
              <el-radio-button label="正常"/>
            </el-radio-group>
          </el-col>

          <el-col span="4">
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
          <el-button @click="addCopy(dynamicValidateForm.copiesISBNs)">添加一个归还副本</el-button>
          <el-button @click="resetForm(dynamicValidateForm)">重置</el-button>
        </el-form-item>
      </el-form>
    </el-main>

  </el-container>
</template>

<script>
import axios from 'axios'

export default {
  name: 'returnBook',
  data() {
    //副本标识验证
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
        radioArray: []
      },
      rules: {
        value: [{required: true, message: '请输入副本标识', trigger: 'blur'}, {
          validator: validateCopy,
          trigger: 'blur'
        }]
      },
      authority: localStorage.getItem('authority') || ['null'],
      ifShowDetails: [],
      copyDetails: [],
      expireCopies: []
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let copiesIsbn = [], ifDamaged = [], ifLost = []
          let noSameIsbn = true;
          for (let i = 0; i < this.dynamicValidateForm.copiesISBNs.length; i++) {
            for (let j = 0; j < i; j++) {
              if (this.dynamicValidateForm.copiesISBNs[i].value === copiesIsbn[j]) {
                noSameIsbn = false
              }
            }
            copiesIsbn[i] = this.dynamicValidateForm.copiesISBNs[i].value
            ifDamaged[i] = this.dynamicValidateForm.radioArray[i] === '损坏';
            ifLost[i] = this.dynamicValidateForm.radioArray[i] === '遗失';
          }
          if (noSameIsbn) {
            axios.post('http://139.196.174.46:8081/returnBook', {
                location: localStorage.getItem('librarianLocation'),
                ifDamaged: ifDamaged,
                ifLost: ifLost,
                copiesIsbn: copiesIsbn,
                admin: localStorage.getItem('userDetails'),
              }
            )
              .then(async resp => {
                if (resp.status === 200) {
                  this.resetForm(this.dynamicValidateForm)
                  this.expireCopies = resp.data
                  for (let i = 0; i < copiesIsbn.length; i++) {
                    if (ifDamaged[i]) {
                      await this.$message.warning('副本 ' + copiesIsbn[i] + ' 损坏，请告知用户在其个人中心查看并缴纳罚款')
                    } else if (ifLost[i]) {
                      await this.$message.warning('副本 ' + copiesIsbn[i] + ' 遗失，请告知用户在其个人中心查看并缴纳罚款')
                    }
                  }

                  let showSuccess = true;
                  for (let i = 0; i < copiesIsbn.length; i++) {
                    if(ifLost[i]) {
                      showSuccess = false
                      break;
                    }
                  }
                  if (showSuccess)
                    await this.$message.success('归还成功!')

                  if (this.expireCopies.length > 0) {
                    for (let i = 0; i < this.expireCopies.length; i++) {
                      await this.$message.warning('副本 ' + this.expireCopies[i] + ' 超期归还，请告知用户在其个人中心查看并缴纳罚款')
                    }
                  }
                } else {
                  this.$message.error('Return error')
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
          this.$set(this.dynamicValidateForm.radioArray, i, this.dynamicValidateForm.radioArray[i + 1])
        }
      }
      this.$set(this.ifShowDetails, this.dynamicValidateForm.copiesISBNs.length - 1, false)
      this.$set(this.copyDetails, this.dynamicValidateForm.copiesISBNs.length - 1, null)
      this.$set(this.dynamicValidateForm.radioArray, this.dynamicValidateForm.copiesISBNs.length - 1, null)
      if (index !== -1)
        this.dynamicValidateForm.copiesISBNs.splice(index, 1)
    },
    addCopy(item) {
      let index = this.dynamicValidateForm.copiesISBNs.length
      this.$set(this.dynamicValidateForm.radioArray, index, '正常')//设置默认状态
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
  //权限拦截
  created() {
    if (this.authority.indexOf('Librarian') === -1) {
      this.$message.error('You have no authority to visit this page')
      this.$router.replace('/')
    }
    this.$set(this.ifShowDetails, 0, false)
    this.$set(this.dynamicValidateForm.radioArray, 0, '正常')
  }
}
</script>

<style scoped>

</style>
