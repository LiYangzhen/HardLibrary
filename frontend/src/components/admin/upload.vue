<template>
  <el-container style="margin: 0 0 60px">

    <el-main class="out-box-white">
      <el-container style="margin-bottom: 20px; margin-left: 10px">
        <h2 style="text-align: left">上新图书</h2>
      </el-container>

      <el-form :model="form" label-width="80px" :rules="rules"
               style=" width: 100%; margin-top: 10px">
        <el-row>
          <el-col :span="7">
            <h3>图书封面</h3>
            <el-form-item prop="image">
              <el-upload
                action="#" list-type="picture-card" :auto-upload="false"
                :before-upload="beforeCoverUpload"
                :on-change="handleChange"
                :on-remove="handleRemove"
                :file-list="fileList" :limit="1"
                style="width: 100%; height: 100%; object-fit: cover">
                <i slot="default" class="el-icon-plus"/>
                <div slot="tip" class="el-upload__tip">*只能上传不大于2MB的图像文件</div>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-divider direction="vertical"/>
          <el-col :span="14" style="margin-left: 5%; margin-right: 5%">
            <el-form-item label="书名" prop="name">
              <el-input v-model="form.name" placeholder="输入书名"/>
            </el-form-item>
            <el-form-item label="作者" prop="author">
              <el-input v-model="form.author" placeholder="输入作者"/>
            </el-form-item>
            <el-form-item label="价格" prop="price">
              <el-input v-model="form.price" placeholder="输入价格"/>
            </el-form-item>
            <el-form-item label="ISBN" prop="isbn">
              <el-input v-model="form.isbn" placeholder="输入图书的ISBN编号"/>
            </el-form-item>
            <el-form-item label="出版时间" prop="date">
              <el-date-picker type="year" placeholder="选择出版年份" v-model="form.date"
                              style="width: 100%;"/>
            </el-form-item>
            <el-form-item label="简介" prop="introduction">
              <el-input v-model="form.introduction" resize="none" type="textarea" placeholder="输入图书简介"/>
            </el-form-item>
            <el-form-item>
              <el-button round type="primary" @click="onSubmit(form)" style="width: 30%">上传&nbsp;<i
                class="el-icon-upload el-icon--right"/></el-button>
            </el-form-item>
          </el-col>
        </el-row>

      </el-form>
    </el-main>

  </el-container>

</template>

<script>

import axios from 'axios'

let imgFile = null
let _form

export default {
  name: 'Upload',

  data() {
    const dataValid = (rule, value, callback) => {
      if (!value)
        return callback(new Error('Can\'t be empty'))
      return callback()
    }
    const priceValid = (rule, value, callback) => {
      if (!/^[0-9]+(.[0-9]{1,2})?$/.test(value)) {
        callback(new Error('请输入非负数，精确到小数点后两位'))
      } else {
        callback()
      }
    }
    return {
      form: {
        name: '',
        author: '',
        isbn: '',
        date: '',
        introduction: '',
        image: null,
        fileData: null,
        price:''
      },
      fileList: [],
      imageUrl: '',
      imageUploaded: 0, // 图片是否已上传（姑且设为数字以防以后可上传多张）
      rules: {
        name: [{required: true, message: '请输入书名', trigger: 'blur'}, {validator: dataValid, trigger: 'blur'}],
        author: [{required: true, message: '请输入作者', trigger: 'blur'}, {validator: dataValid, trigger: 'blur'}],
        date: [{required: true, message: '请输入出版时间', trigger: 'blur'}, {validator: dataValid, trigger: 'blur'}],
        isbn: [{required: true, message: '请输入ISBN', trigger: 'blur'}, {validator: dataValid, trigger: 'blur'}],
        location: [{required: true, message: '请选择地址', trigger: 'blur'}, {validator: dataValid, trigger: 'blur'}],
        introduction: [{required: true, message: '请输入简介', trigger: 'blur'}, {validator: dataValid, trigger: 'blur'}],
        price: [{required: true, message: '请输入价格', trigger: 'blur'}, {validator: priceValid, trigger: 'blur'}],
      },
      dialogImageUrl: '',
      dialogVisible: false,
      disabled: false,
      authority: localStorage.getItem('authority') || ['null']
    }
  },
  watch: {
    form: function (val) {
      _form = val
    }
  },
  methods: {
    //必须填写所有项目才能上传
    judgeForm() {
      return (_form.name !== '' &&
        _form.author !== '' &&
        _form.isbn !== '' &&
        _form.introduction !== '' &&
        _form.date !== '' &&
        _form.price !== '' &&
        imgFile !== null)
    },
    onSubmit() {
      if (this.judgeForm()) {
        let formData = new FormData()
        formData.append('name', this.form.name)
        formData.append('author', this.form.author)
        formData.append('isbn', this.form.isbn)
        formData.append('date', this.form.date)
        formData.append('introduction', this.form.introduction)
        formData.append('image', this.fileData)
        formData.append('price', parseFloat(this.form.price))
        axios({
          method: 'post',
          url: 'http://139.196.174.46:8081/upload',
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          withCredentials: true,
          data: formData
        })
          .then(resp => {
            if (resp.status === 200) {
              this.$message.success('book successfully uploaded!')
              this.$router.replace('/')
              //上传成功后需清除相应变量
              imgFile = null
            } else {
              this.$message.error('upload error')
            }
          })
          // eslint-disable-next-line handle-callback-err
          .catch(error => {
            this.$message.error(error.response.data.message)
          })
      } else {
        this.$message.error('please fill in all the required fields')
      }
    },
    beforeCoverUpload(file) {
      const isJPG = file.type === 'image/jpeg'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG)
        this.$message.error('上传封面图片只能是 JPG 格式!')
      if (!isLt2M)
        this.$message.error('上传封面图片大小不能超过 12MB!')

      return isJPG && isLt2M
    },
    handleChange(file, fileList) {
      this.imageUploaded += 1
      imgFile = file
      this.imageUrl = URL.createObjectURL(file.raw)
      this.fileData = file.raw
      this.fileList = fileList.slice(-3)
    },
    //移除图片
    handleRemove(file, fileList) {
      this.fileData = null
      imgFile = null
    }
  },
  //权限拦截
  created() {
    if (this.authority.indexOf('Librarian') === -1) {
      this.$message.error('You have no authority to visit this page')
      this.$router.replace('/')
    } else {
      _form = this.form
    }
  }
}
</script>

<style scoped>

</style>
