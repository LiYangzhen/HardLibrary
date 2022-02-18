<template>
  <div id="base_login"> <!-- 此处与下一行必须为div否则无法显示！ -->
    <div class="block" style="z-index: -1">
      <el-carousel height="100vh" direction="horizontal" :autoplay="true">
        <el-carousel-item>
          <img src="../../assets/background/background_norwaysnow.jpg"
               class="carousel-img"
               width="100%"
               style="object-fit: cover;" alt=""/>
        </el-carousel-item>
        <el-carousel-item>
          <img src="../../assets/background/background_guilin.jpg"
               class="carousel-img"
               width="100%"
               style="object-fit: cover;" alt=""/>
        </el-carousel-item>
        <el-carousel-item>
          <img src="../../assets/background/background_night.jpg"
               class="carousel-img"
               width="100%"
               style="object-fit: cover;" alt=""/>
        </el-carousel-item>
        <el-carousel-item>
          <img src="../../assets/background/background_traingirl.jpg"
               class="carousel-img"
               width="100%"
               style="object-fit: cover;" alt=""/>
        </el-carousel-item>
        <el-carousel-item>
          <img src="../../assets/background/background_catgirl.jpg"
               class="carousel-img"
               width="100%"
               style="object-fit: cover;" alt=""/>
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
    <el-container id="div-form" autofocus="autofocus" style="z-index: 9">
      <el-form :model="loginForm"
               :rules="rules"
               class="login_container"
               label-position="left"
               label-width="0px"
               v-loading="loading"
               :ref="loginForm"
               size="small"
               style="z-index: 10">
        <h3 class="login_title" v-if="reader">Login</h3>
        <h3 class="login_title" v-else>Librarian Login</h3>
        <br>
        <el-row>
          <el-col :span="22" style="margin-left: 4.5%">
            <el-form-item prop="username">
              <el-input type="text"
                        v-model="loginForm.username"
                        auto-complete="off"
                        placeholder="Username ID"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="22" style="margin-left: 4.5%">
            <el-form-item prop="password">
              <el-input type="password"
                        v-model="loginForm.password"
                        auto-complete="off"
                        placeholder="Password"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item prop="usertype">
          <el-radio-group v-model="loginForm.usertype"
                          @change="changeRole">
            <el-radio-button label="Reader" border class="user-type">
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Reader&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </el-radio-button>
            <el-radio-button label="Librarian" border class="user-type">
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Librarian&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-radio-group v-model="loginForm.radio" v-if="!reader" size="medium"
                        style="margin-top: 0px; margin-bottom: 20px">
          <el-radio-button label="邯郸"/>
          <el-radio-button label="江湾"/>
          <el-radio-button label="枫林"/>
          <el-radio-button label="张江"/>
        </el-radio-group>
        <el-form-item>
          <div class="details-btn-container">
            <div class="details-btn">
              <a v-on:click="login(loginForm)">Login</a>
            </div>
          </div>
        </el-form-item>
        <span id="login2register" v-if="reader">Don't have an account?
          <router-link to="register"><a><i>Register here</i></a></router-link>!
        </span>

      </el-form>
    </el-container>

  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Login',

  data() {
    return {
      //默认选中读者身份，管理员默认图书馆为邯郸图书馆
      loginForm: {
        username: '',
        password: '',
        usertype: 'Reader',
        radio: '邯郸'
      },
      rules: {
        username: [{required: true, message: '', trigger: 'blur'}],
        password: [{required: true, message: '', trigger: 'blur'}]
      },
      loading: false,
      reader: true
    }
  },

  methods: {
    login(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          let _this = this
          let formData = new FormData()
          formData.append('username', this.loginForm.username)
          formData.append('password', this.loginForm.password)
          axios({
            method: 'post',
            url: 'http://139.196.174.46:8081/login',
            headers: {
              'Content-Type': 'multipart/form-data'
            },
            withCredentials: true,
            data: formData
          })
            .then(resp => {
              if (resp.status === 200) {
                if (resp.data.authorities.indexOf('Reader') > -1 && _this.reader) {
                  this.$store.commit('login', resp.data)
                  //页面重定向
                  let redirect = decodeURIComponent(this.$route.query.redirect || '/')
                  this.$router.replace({path: redirect})
                  //存储用户权限的信息
                  localStorage.setItem('authority', resp.data.authorities)
                } else if ((resp.data.authorities.indexOf('Librarian') > -1  || resp.data.authorities.indexOf('Admin') > -1) && !_this.reader) {
                  this.$store.commit('login', resp.data)
                  let redirect = decodeURIComponent(this.$route.query.redirect || '/')
                  this.$router.replace({path: redirect})
                  //存储管理员登录地点的信息
                  localStorage.setItem('librarianLocation', this.loginForm.radio + '图书馆')
                  localStorage.setItem('authority', resp.data.authorities)
                } else {
                  //登录身份与账号不符
                  this.$message.error('authorities error')
                }
              } else {
                this.$message.error('login error')
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
    //改变登录身份
    changeRole() {
      if (this.loginForm.usertype === 'Reader') {
        this.reader = true
      } else {
        this.reader = false
      }
    }
  },
  created() {
    if (this.$route.query.info) {
      this.$message.error(this.$route.query.info || '')
    }
  }

}
</script>

<style scoped>

  .carousel-img {
    min-height: 100%;
  }

  @media screen and (max-width: 600px) { /* 响应式布局 */
    #div-form {
      position: fixed;
      top: 20vh;
      left: 10vw;
      z-index: 2;
    }

  }


#base_login {
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
  top: 16vh;
  right: 10vw;
  z-index: 2;

}

.login_container {
  border-radius: 0.6em;
  background-clip: padding-box;
  margin: 0;
  width: 18em;
  padding: 3em 2.5em;
  background: linear-gradient(#441378, #89afff);
  border: 1px solid #eaeaea;
  box-shadow: 0 0 2em #cac6c6;
}

.login_title {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 30px;
  font-family: "Times New Roman";
  color: #fff;
}

#login2register {
  font-size: 12px;
  color: #505458;
  margin-bottom: 1em;
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
    background-color: #7ac7ff;
    box-shadow: 0 0 5px #7ac7ff,
    0 0 15px #7ac7ff,
    0 0 30px #7ac7ff,
    0 0 60px #7ac7ff;
  }

</style>
