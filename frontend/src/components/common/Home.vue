<template>

  <el-container id="base_main">


    <el-container>
      <el-container>
        <el-header style="height: 64px; font-size: 14px; text-align: left; background-color: rgb(48,114,184); color: #ffffff">
          <!-- 顶端登录、个人中心栏 -->
          <el-menu class="el-menu-demo" mode="horizontal" :default-active="'0-1'" background-color="rgb(48, 114, 184)"
                   text-color="#fff" active-text-color="#87ddff" @select='handleSelect'>
            <span style="float: left; margin-left: 1%; margin-right: 10%">
              <span class="home-title">HardLibrary</span>
            </span>
            <el-submenu index="0" class="menu-top-item">
              <template slot="title" class="menu-top-item">
                <i class="el-icon-s-home"/>
                <span slot="title" class="menu-top-item"><b class="menu-top-item">在线阅览</b></span>
              </template>
              <el-menu-item index="0-1" class="menu-item">
                <i class="el-icon-s-grid" style="margin-left: 20%"/>全部书籍
              </el-menu-item>
              <el-menu-item index="0-2" class="menu-item">
                <i class="el-icon-search" style="margin-left: 20%"/>查询书籍
              </el-menu-item>
            </el-submenu>
            <el-submenu index="1" v-if="authority.indexOf('Librarian') > -1" class="menu-top-item">
              <template slot="title">
                <i class="el-icon-reading"/>
                <span slot="title"><b>书籍管理</b></span>
              </template>
              <el-menu-item index="1-1" class="menu-item">
                <i class="el-icon-document-add" style="margin-left: 20%"/>上新图书
              </el-menu-item>
              <el-menu-item index="1-2" class="menu-item">
                <i class="el-icon-edit" style="margin-left: 20%"/>借阅登记
              </el-menu-item>
              <el-menu-item index="1-3" class="menu-item">
                <i class="el-icon-receiving" style="margin-left: 20%"/>图书归还
              </el-menu-item>
              <el-menu-item index="1-4" class="menu-item">
                <i class="el-icon-s-check" style="margin-left: 20%"/>图书领取
              </el-menu-item>
              <el-menu-item index="1-5" class="menu-item">
                <i class="el-icon-search" style="margin-left: 20%"/>记录查询
              </el-menu-item>
              <el-menu-item index="1-6" class="menu-item">
                <i class="el-icon-star-off" style="margin-left: 20%"/>书评管理
              </el-menu-item>
            </el-submenu>
            <el-submenu index="2" v-if="authority.indexOf('Admin') > -1" class="menu-top-item">
              <template slot="title"><i class="el-icon-s-custom"/>
                <span slot="title"><b>超管权限</b></span>
              </template>
              <el-menu-item index="2-1" class="menu-item">
                <i class="el-icon-setting" style="margin-left: 20%"/>系统设置
              </el-menu-item>
            </el-submenu>
            <el-submenu index="3" v-if="authority.indexOf('null') === -1" class="menu-top-item">
              <template slot="title"><i class="el-icon-user"/>
                <span slot="title"><b>个人中心</b></span>
              </template>
              <el-menu-item index="3-1" class="menu-item" v-if="authority.indexOf('Reader') > -1 || authority.indexOf('Librarian') > -1">
                <i class="el-icon-s-order" style="margin-left: 20%"/>个人中心
              </el-menu-item>
              <el-menu-item index="3-2" class="menu-item" v-if="authority.indexOf('Reader') > -1">
                <i class="el-icon-star-on" style="margin-left: 20%"/>图书评价
              </el-menu-item>
            </el-submenu>

            <span style="float: right; margin-right: 0">
              <span v-if="username === null" style="position: relative; right: 0">
                <el-divider direction="vertical"/>
                <router-link to="/login" class="home-login-button">Login</router-link>
              </span>

              <el-dropdown @command="handleCommand" style="color: #eee">
                <i class="el-icon-setting" v-if="username !== null" style="margin-right: 10px">
                  <span v-if="username !== null">&nbsp;&nbsp;{{ username }}</span>
                  <span v-if="librarianLocation !== null">&nbsp;&nbsp;{{ librarianLocation }}</span>
                </i>
                <el-dropdown-menu slot="dropdown" style="margin-left: 10px;" v-if="username !== null">
                  <el-dropdown-item v-if="authority.indexOf('Reader') > -1 || authority.indexOf('Librarian') > -1">
                    <router-link to="/userDetails" style="text-decoration: none; color: #666"><i class="el-icon-user"/>&nbsp;&nbsp;个人中心&nbsp;&nbsp;</router-link>
                  </el-dropdown-item>
                  <el-dropdown-item command="logout">
                    <i class="el-icon-back"/>&nbsp;&nbsp;退出登录&nbsp;&nbsp;
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </span>

          </el-menu>




        </el-header>

        <el-main style="background-color: rgba(66,155,255,0.07)">
          <router-view>
          </router-view>
        </el-main>

        <el-footer height="40px" style="position:fixed; bottom: 0; width: 100%; z-index: 999; font-size: 12px; background-color: rgb(48,114,184); color: #ffffff">
          <div style="line-height: 40px; margin-top: 1px;">
            2021软件工程_第5小组&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;超管admin 密码qwe123
          </div>
        </el-footer>

      </el-container>
    </el-container>

  </el-container>

</template>

<script>

  import store from '../../store'

  export default {
    name: 'home',
    watch: {
      cur: function (oldValue, newValue) {
        console.log(oldValue, newValue)
      }
    },

    data() {
      return {
        show: false,
        isCollapse: true,
        activeIndex: '1',
        activeIndex2: '1',
        input: '',
        direction: 'ltr',
        username: localStorage.getItem('userDetails') || null,
        librarianLocation: localStorage.getItem('librarianLocation') || null,
        authority: localStorage.getItem('authority') || ['null']
      }
    },
    methods: {
      //侧边栏按钮跳转
      handleSelect(key) {
        switch (key) {
          case '0-1':
            this.$router.push('/browse');
            break;
          case '0-2':
            this.$router.push('/searchResult');
            break;
          case '1-1':
            this.$router.push('/upload');
            break;
          case '1-2':
            this.$router.push('/borrowBook');
            break;
          case '1-3':
            this.$router.push('/returnBook');
            break;
          case '1-4':
            this.$router.push('/getReservedBook');
            break;
          case '1-5':
            this.$router.push('/historyManagement');
            break;
          case '1-6':
            this.$router.push('/ratingsManagement');
            break;
          case '2-1':
            this.$router.push('/systemSettings');
            break;
          case '3-1':
            this.$router.push('/userDetails');
            break;
          case '3-2':
            this.$router.push('/ratings');
            break;
          default:
            break;
        }
      },
      handleOpen(key, keyPath) {
        // console.log(key, keyPath);
      },
      handleClose(key, keyPath) {
        // console.log(key, keyPath);
      },
      //改变侧边栏伸缩
      changeCollapse() {
        this.isCollapse = !this.isCollapse;
        this.show = !this.show
      },
      handleCommand(command) {
        if (command === 'logout') {
          store.commit('logout');
          this.$router.push('/login')
        }
      }
    }
  }
</script>

<style>

  /* 以下灰掉的代码有效！勿删除！！ */
  .el-header, .el-footer {
    /*background: linear-gradient(to top, #2963a1, #367bc9);*/
    text-align: center;
    line-height: 60px;
  }
  .el-header {
    /*background: linear-gradient(to top, #367bc9, #2963a1);*/
  }

  .el-main {
    /*background: linear-gradient(to top, #d3cce3, #e9e4f0);*/
    background-color: #f0faff;
    /*background-color: #fff;*/
    color: #333;
    text-align: center;
  }

  .el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
  }

  .el-select .el-input {
    width: 130px;
  }

  .page-bar {
    margin: 40px;
  }

  .page-bar li:first-child > a {
    margin-left: 0
  }

  .page-bar a {
    border: 1px solid #ddd;
    text-decoration: none;
    position: relative;
    float: left;
    padding: 6px 12px;
    margin-left: -1px;
    line-height: 1.42857143;
    color: #337ab7;
    cursor: pointer
  }

  .page-bar a:hover {
    background-color: #eee;
  }

  .page-bar .active a {
    color: #fff;
    cursor: default;
    background-color: #337ab7;
    border-color: #337ab7;
  }

  .page-bar i {
    font-style: normal;
    color: #d44950;
    margin: 0 4px;
    font-size: 12px;
  }

  .home-title {
    font-size: 28px;
    font-family: Garamond;
    font-weight: bold;
    transition: 5s;
  }

  .home-title:hover {
    margin-left: 8.5vw;
    cursor: none;
  }

  .home-login-button {
    text-decoration: none;
    font-size: 20px;
    font-family: Garamond;
    font-weight: bold;
    color: #fff;
    transition: 0.5s;
  }

  .home-login-button:hover {
    color: #90e5ed;
  }

  .el-menu-vertical-demo {
    /*padding-right: 30px;*/
  }

  .el-menu-vertical-demo:not(.el-menu--collapse) {
    min-width: 20%;
    min-height: 400px;
    padding-right: 50px;
  }

  @media screen and (max-width: 600px) {
    /* 响应式布局 */
    .el-menu-vertical-demo:not(.el-menu--collapse) {
      min-width: 100%;
    }
  }

  ul, li {
    margin: 0;
    padding: 0;
  }

  li {
    list-style: none
  }

  h2 {
    font-size: 28px;
    color: #3f475a;
    letter-spacing: 2px;
  }

  h3 {
    text-align: center;
    color: #3f475a;
    margin-bottom: 25px;
    margin-left: 2%;
  }

  /* 勿删！用于图书管理员&读者各种功能界面的el-main样式 */
  .out-box {
    background-color: rgba(158, 211, 245, 0.05);
    border: 15px solid rgb(56,141,220);
    border-radius: 0 0 20px 20px;
  }

  .out-box-white {
    background-color: #fdfdfd;
    border: 1px solid #ccc;
    border-radius: 20px;
    box-shadow: 0 0 10px rgb(180,180,180);
    padding-left: 3%;
    padding-right: 3%;
  }

  .utilities-page-el-main {
    margin-left: 20%;
    margin-right: 20%;
    padding-top: 30px;
    padding-left: 3%;
    padding-right: 5%;
    background-color: #fdfdfd;
    border: 1px solid #ccc;
    border-radius: 10px;
    box-shadow: 0 0 10px rgb(180,180,180);
  }

  .utilities-page-el-container {
    margin: 4vh 0;
  }

  .utilities-page-submit-buttons {
    margin-left: -7%;
  }

  .utilities-page-input {
    width: 80%;
  }

  .utilities-page-delete-button {
    width: 90%;
    margin-left: 10%;
  }

  .submenu-title {
    margin-left: -60%;
  }

  .menu-item {
    height: 50px !important;
    line-height: 50px !important;
  }

  .el-icon-setting:hover {
    color: #87ddff;
    cursor: pointer;
  }


  /* 按钮发光效果 */
  .details-btn-container {
    width: 200px;
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    font-family: "Times New Roman";
  }

  .details-btn-container .details-btn {
    position: relative;
    width: 150px;
    height: 50px;
    margin: 20px;
  }

  .details-btn-container .details-btn a {
    text-decoration: none;
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    background: rgba(235, 235, 235, 0.05);
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 30px;
    color: #666;
    z-index: 1;
    font-weight: 400;
    letter-spacing: 1px;
    overflow: hidden;
    transition: 0.5s;
    backdrop-filter: blur(20px);
  }

  .details-btn-container .details-btn:hover a {
    letter-spacing: 3px;
  }

  .details-btn-container .details-btn a::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 50%;
    height: 100%;
    background: linear-gradient(to left, rgba(255, 255, 255, 0.15), transparent);
    transform: skew(45deg) translateX(0);
    transition: 0.5s;
  }

  .details-btn-container .details-btn:hover a::before {
    transform: skew(45deg) translateX(100px);
  }

  .details-btn-container .details-btn::before {
    content: '';
    position: absolute;
    left: 50%;
    width: 30px;
    height: 10px;
    bottom: -5px;
    transform: translateX(-50%);
    border-radius: 10px;
    transition: 0.5s;
  }

  .details-btn-container .details-btn:hover:before {
    bottom: 0px;
    width: 100%;
    height: 50%;
    border-radius: 0 0 20px 20px;
  }

  .details-btn-container .details-btn::after {
    content: '';
    position: absolute;
    left: 50%;
    width: 30px;
    height: 10px;
    top: -5px;
    transform: translateX(-50%);
    border-radius: 10px;
    transition: 0.5s;
  }

  .details-btn-container .details-btn:hover:after {
    top: 0px;
    width: 100%;
    height: 50%;
    border-radius: 20px 20px 0 0;
  }

  .details-btn-container .details-btn::before,
  .details-btn-container .details-btn::after {
    background-color: #2bd2ff;
    box-shadow: 0 0 5px #2bd2ff,
    0 0 15px #2bd2ff,
    0 0 30px #2bd2ff,
    0 0 60px #2bd2ff;
  }

  .menu-item:hover{
    background-color: #43d2ff !important;
  }

  .el-submenu__title:hover{
    background-color: #43d2ff !important;
  }
</style>

