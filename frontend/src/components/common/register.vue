<template>
  <div id="base_register"> <!-- 此处与下一行必须为div否则无法显示！ -->
    <div class="block" style="z-index: -1">
      <el-carousel height="100vh" direction="horizontal" :autoplay="true">
        <el-carousel-item>
          <img src="../../assets/background/background_alpes.jpg" class="carousel-img" width="100%" alt=""/>
        </el-carousel-item>
        <el-carousel-item>
          <img src="../../assets/background/background_road.jpg" class="carousel-img" width="100%" alt=""/>
        </el-carousel-item>
        <el-carousel-item>
          <img src="../../assets/background/background_norwaysnow.jpg" class="carousel-img" width="100%" alt=""/>
        </el-carousel-item>
        <el-carousel-item>
          <img src="../../assets/background/background_paris.jpg" class="carousel-img" width="100%" alt=""/>
        </el-carousel-item>
        <el-carousel-item>
          <img src="../../assets/background/background_traingirl.jpg" class="carousel-img" width="100%" alt=""/>
        </el-carousel-item>
      </el-carousel>
    </div>

    <el-container id="div-return" style="z-index: 8">
      <router-link to="/">
        <el-button type="warning" round>
          <i class="el-icon-back"/>&nbsp;&nbsp;返回主页
        </el-button>
      </router-link>
    </el-container>

    <el-container id="div-form" autofocus="autofocus" style="z-index: 9;">
      <el-form :model="registerForm"
               :rules="rules"
               class="register_container"
               label-position="left"
               label-width="0px"
               v-loading="loading"
               :ref="registerForm"
               size="small"
               style="z-index: 10;">
        <h3 class="register_title">Register</h3>
        <br>
        <el-row>
          <el-col :span="22" style="margin-left: 4.5%">
            <el-form-item prop="username">
              <el-input type="text"
                        v-model="registerForm.username"
                        auto-complete="off"
                        placeholder="Username ID"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="22" style="margin-left: 4.5%">
            <el-form-item prop="password">
              <el-input type="password"
                        v-model="registerForm.password"
                        autocomplete="off"
                        placeholder="Password"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="22" style="margin-left: 4.5%">
            <el-form-item prop="checkPassword">
              <el-input type="password"
                        v-model="registerForm.checkPassword"
                        autocomplete="off"
                        placeholder="Confirm Password"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="22" style="margin-left: 4.5%">
            <el-form-item prop="email">
              <el-input type="text"
                        v-model="registerForm.email"
                        auto-complete="off"
                        placeholder="Email"
                        disabled="true"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="13.5" style="margin-left: 4.5%; margin-right: 1%">
            <el-form-item prop="code">
              <el-input type="text"
                        v-model="registerForm.code"
                        auto-complete="off"
                        placeholder="6-digit Verification Code"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-button type="primary" @click="sendVerificationCode()" style="line-height: 6px">Send VC</el-button>
          </el-col>
        </el-row>

        <el-form-item prop="usertype">
          <el-radio-group v-model="registerForm.usertype">
            <el-row>
              <el-col :span="7">
                <el-radio-button label="Teacher" class="user-type">Teacher</el-radio-button>
              </el-col>
              <el-col :span="9">
                <el-radio-button label="Postgraduate" class="user-type" style="margin-right: 11px">Postgraduate</el-radio-button>
              </el-col>
              <el-col :span="8">
                <el-radio-button label="Undergraduate" class="user-type">Undergraduate</el-radio-button>
              </el-col>
            </el-row>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <div class="details-btn-container">
            <div class="details-btn">
              <a v-on:click="register(registerForm)">Register</a>
            </div>
          </div>
        </el-form-item>
      </el-form>
    </el-container>

  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Register',

  data() {
    const dataValid = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('Can\'t be empty'))
      }
      return callback()
    }
    //验证邮箱格式
    const validateEmail = (rule, value, callback) => {
      if (!/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(value)) {
        callback(new Error('邮箱格式错误'))
      } else {
        callback()
      }
    }
    //验证用户名格式
    const validateUsername = (rule, value, callback) => {
      if (!/^[0-9]{11}$/.test(value)) {
        callback(new Error('请输入11位学号'))
      } else {
        callback()
      }
    }
    //验证密码格式
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
    //验证两次密码是否一致
    const validateCheckPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }

    return {
      registerForm: {
        username: '',
        password: '',
        email: '',
        usertype: '',
        checkPassword: ''
      },
      rules: {
        // blur 失去鼠标焦点时触发验证
        username: [{required: true, message: '请输入学号/工号', trigger: 'blur'}, {
          validator: validateUsername,
          trigger: 'blur'
        },
          {min: 11, max: 11, message: '请输入11位学号'}],
        password: [{required: true, message: '请输入密码', trigger: 'blur'}, {validator: validatePassword, trigger: 'blur'},
          {min: 6, max: 32, message: '密码长度在6-32个字符'}],
        email: [{required: true, message: '请输入邮箱', trigger: 'blur'}, {validator: validateEmail, trigger: 'blur'}],
        usertype: [{required: true, message: '请选择用户类型', trigger: 'blur'}, {validator: dataValid, trigger: 'blur'}],
        checkPassword: [{required: true, message: '请重新输入密码', trigger: 'blur'}, {
          validator: validateCheckPassword,
          trigger: 'blur'
        }]
      },
      loading: false
    }
  },
  //动态将邮箱设为用户名加后缀
  watch: {
    'registerForm.username': function (val) {
      this.registerForm.email = val + '@fudan.edu.cn'
    }
  },

  methods: {
    register(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          let authorities;
          if (this.registerForm.usertype === 'Teacher')
            authorities = ['Reader', 'Teacher']
          else if (this.registerForm.usertype === 'Undergraduate' || this.registerForm.usertype === 'Postgraduate')
            authorities = ['Reader', 'Student', this.registerForm.usertype]
          axios.post('http://139.196.174.46:8081/register', {
              username: this.registerForm.username,
              password: this.registerForm.password,
              email: this.registerForm.email,
              verificationCode: this.registerForm.code,
              authorities: authorities,
            }
          )
            .then(resp => {
              // 根据后端的返回数据修改
              if (resp.status === 200 && resp.data.hasOwnProperty('id')) {
                this.$message.success('Successful registration')
                // 跳转到login
                this.$router.replace('/login')
              } else {
                this.$message.error('Register error')
              }
            })
            .catch(error => {
              this.$message.error(error.response.data.message)
            })
        } else {
          this.$message.error('Please fill in all input boxes validly')
        }
      })
    },

    sendVerificationCode() {
      let formData = new FormData()
      let email = this.registerForm.email
      if (email !== "" && /^[0-9]{11}@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(email)) {
        formData.append('email', this.registerForm.email)
        axios({
          method: 'post',
          url: 'http://139.196.174.46:8081/sendVerificationCode',
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          withCredentials: true,
          data: formData
        })
        this.$message.success('Verification code sent')
      } else {
        this.$message.error('Email address pattern invalid')
      }

    }

  }
}
</script>

<style scoped>

  .carousel-img {
    min-height: 100%;
    object-fit: cover;
  }

  @media screen and (max-width: 600px) { /* 响应式布局 */
    #div-form {
      position: fixed;
      top: 20vh;
      left: 10vw;
      z-index: 2;
    }

  }

#base_register {
  height: 100%;
  width: 100%;
  background-size: cover;
  position: fixed;
}

#div-return {
  position: fixed;
  float: left;
  top: 1.5vh;
  left: 1.2vw;
  z-index: 2;
}

#div-form {
  position: fixed;
  float: right;
  top: 12vh;
  right: 10vw;
  z-index: 2;
}

.register_container {
  border-radius: 0.6em;
  background-clip: padding-box;
  margin: 0;
  width: 18em;
  height: 27em;
  padding: 3em 2.5em;
  background: linear-gradient(#441378, #89afff);
  border: 1px solid #eaeaea;
  box-shadow: 0 0 2em #cac6c6;
}

.user-type {

}

.register_title {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 30px;
  font-family: "Times New Roman";
  color: #fff;
}



  .details-btn-container{
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    font-family: "Times New Roman";
  }
  .details-btn-container .details-btn{
    position: relative;
    width: 100%;
    height: 50px;
    margin: 20px;
  }
  .details-btn-container .details-btn a{
    color: #fff;
  }
  .details-btn-container .details-btn::before,
  .details-btn-container .details-btn::after{
    background-color: #41e954;
    box-shadow: 0 0 5px #41e954,
    0 0 15px #41e954,
    0 0 30px #41e954,
    0 0 60px #41e954;
  }

</style>
