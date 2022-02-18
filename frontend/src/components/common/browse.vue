<template>
  <el-container>
    <div class="scene" v-if="this.books[0] == null">
      <div class="rocket">
        <img src="../../assets/misc/rocket.png" alt="">
      </div>
    </div>

    <el-main style="background: transparent;display: flex;flex-flow: row wrap;justify-content: center">
      <el-container class="card-container" v-for="book in this.books" v-bind:key="book.isbn">
        <el-card class="box-card" :key="book.index">
          <el-container class="box-top">
            <div class="image">
              <el-image class="box-image" @click="toDetails(book.isbn)"
                        :src="'data:image/jpeg;base64,'+ book.image" :fit="fit"
                        style="cursor: pointer"/>
            </div>
          </el-container>
          <div class="box-bottom">
            <el-row>
              <h2 v-if="book.name.length < 20">{{ book.name }}</h2>
              <h2 v-else>{{ book.name.substr(0, 18) + "..." }}</h2>
            </el-row>
            <el-row>
              <span v-if="book.author.length < 40">{{ book.author }}</span>
              <span v-else>{{ book.author.substr(0, 38) + "..." }}</span>
            </el-row>
            <el-row style="margin-top: 10px">
              <!-- ### WRITE YOUR CODE HERE ### -->
              <el-rate v-model="book.rating" disabled show-score :colors="starColors" :text-color="starColors" text-color="#ff9900"/>
            </el-row>
          </div>
          <el-container class="box-bottom2">
            <ul>
              <li v-if="book.name.length < 20">图书名称：{{ book.name }}</li>
              <li v-else>图书名称：{{ book.name.substr(0, 18) + "..." }}</li>
              <li v-if="book.author.length < 40">图书作者：{{ book.author }}</li>
              <li v-else>图书作者：{{ book.author.substr(0, 38) + "..." }}</li>
              <li>图书编号：{{ book.isbn }}</li>
              <li v-if="book.introduction.length < 27">图书简介：{{ book.introduction }}</li>
              <li v-else>图书简介：{{ book.introduction.substr(0, 24) + "..." }}</li>
              <li>
                <el-button type="text" class="details-btn-browse" @click="toDetails(book.isbn)">详情</el-button>
              </li>
            </ul>
          </el-container>

        </el-card>
      </el-container>
    </el-main>

    <el-footer style="background: transparent">
      <el-row class="page-bar">
        <div class="block">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :page-sizes="[6, 9, 12, 18]"
            :page-size="9"
            :hide-on-single-page="true"
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalElements"
          ><!-- 参数page-size：默认状态下每页采用几张图 -->
          </el-pagination>
        </div>
      </el-row>
    </el-footer>

  </el-container>
</template>

<script>
import axios from 'axios'

export default {
  name: 'browse',

  data() {
    return {
      fit: 'cover',
      books: [ ],
      totalPages: 1,
      totalElements: 0, //总共书本数量
      booksPerPage: 9, // 一页展示的数量
      cur: 1, // 当前页码
      starColors: ['#99A9BF', '#F7BA2A', '#FF9900'],
    }
  },
  //页面创建时，向后端请求所有书本的数据
  created() {
    let _this = this
    axios.get('http://139.196.174.46:8081/findBooks/' + ( _this.cur - 1 ) + '/' + _this.booksPerPage).then(function (resp) {
      _this.books = resp.data.content
      _this.totalPages = resp.data.totalPages
      _this.totalElements = resp.data.totalElements
    })
  },


  methods: {
    //跳转到书本详情页
    toDetails: function (isbn) {
      this.$router.push({path: 'details', query: {isbn: isbn}})
    },
    //改变一页显示书本数的方法，需重新向后端请求
    handleSizeChange: function (val) {
      if (val !== this.booksPerPage) {
        this.booksPerPage = val
        let _this = this
        axios.get('http://139.196.174.46:8081/findBooks/' + ( _this.cur - 1 ) + '/' + _this.booksPerPage).then(function (resp) {
          _this.books = resp.data.content
          _this.totalPages = resp.data.totalPages
          _this.totalElements = resp.data.totalElements
        })
      }
    },
    //改变当前页码的方法，需重新向后端请求
    handleCurrentChange: function (val) {
      if (val !== this.cur) {
        this.cur = val
        let _this = this
        axios.get('http://139.196.174.46:8081/findBooks/' + ( _this.cur - 1 ) + '/' + _this.booksPerPage).then(function (resp) {
          _this.books = resp.data.content
          _this.totalPages = resp.data.totalPages
          _this.totalElements = resp.data.totalElements
        })
      }
    },

  }

}

</script>

<style scoped>

a {
  color: #409EFF;
  text-decoration: underline;
}

.page-bar {
  margin-top: 3em;
  margin-bottom: 4em;
}


.details-btn-browse {
  margin-left: 78%;
  padding: 3px 0;
  transition: 0.25s;
}

.details-btn-browse:hover {
  font-size: 16px;
}



.card-container{
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
}

.box-card{
  position: relative;
  width: 24vw;
  height: 320px;
  margin:0 30px 50px;
  background-color: rgb(255,255,255);
  box-shadow: 0 0 10px rgb(150,150,150);
  overflow: hidden;
  transition: 0.7s;
  border-radius: 0 0 30px 30px;
}

.box-card:hover{
   transform: translateY(-10px);
 }

/* -------------------------------------------- */
/* 上部分的块 */
/* -------------------------------------------- */
.box-top{
  position: relative;
  width: 100%;
  height: 150px;
  background-color: rgb(158, 211, 245);
  display: flex;
  justify-content: center;
  transition: .7s;
}
.box-top::after{
  content: '';
  width: 12%;
  height: 40px;
  clip-path: polygon(50% 40%,0 0,100% 0);
  background-color: rgb(70, 94, 137);
  position: absolute;
  left: 50%;
  transform: translate(-50%);
  bottom: -38px;
  z-index: 999;
}
.image{
  position: absolute;
  top: 10px;
  width: 120px;
  height: 120px;
  background-size: cover;
  border: 5px solid rgb(80, 105, 153);
  transition: .7s;
}
.box-image{
  width: 120px;
  height: 120px;
  transition: .7s;
}

/* -------------------------------------------- */
/* 下部分的两个大块 */
/* -------------------------------------------- */
.box-bottom,.box-bottom2{
  position: absolute;
  top: 160px; /* box-top height + margintop */
  width: 87%;
  margin-left: 0%;
  margin-right: 13%;
  height: 170px;
  text-align: center;
  transition: 0.7s;
}
.box-bottom h2{
  display: block;
  margin: 30px 0 10px 0;
  font: 800 20px '微软雅黑 Light';
  color: #3f475a;
}
.box-bottom span{
  font: 400 18px '华文细黑';
  color: #3f475a;
}
.box-bottom h2::after{ /* 横线分割线 */
  content: '';
  display: block;
  width: 90%;
  height: 2px;
  background-color: #8da2ca;
  position: absolute;
  left: 50%;
  transform: translate(-50%,4px);
}
/* -------------------------------------------- */
/* 下部分的第二个大块 */
/* -------------------------------------------- */
.box-bottom2{
  top: 330px;
  height: 240px;
}
.box-bottom2 ul{
  width: 100%;
}

.box-bottom2 li{
  margin: 10px 0px;
  font: 800 14px '微软雅黑 Light';
  transition: .3s;
  color: #4d5569;
}
/* -------------------------------------------- */
/* 鼠标移入的效果 */
/* -------------------------------------------- */
.box-card:hover .box-top{
  height: 50px;
}
.box-card:hover .image{
  width: 35px;
  height: 35px;
  border-width: 3px;
  transform: translate(0vw,-6px);
}
.box-card:hover .box-image{
  width: 35px;
  height: 35px;
  border-width: 3px;
}

.box-card:hover .box-bottom{
  top: 300px;
}
.box-card:hover .box-bottom2{
  top: 90px;
}
li:hover{
  color: #8c9dbe;
}



@media screen and (max-width: 600px) { /* 手机端响应式布局 */
  .box-card {
    width: 90vw;
  }
}

/*@media screen and (max-width: 600px) { !* 手机端响应式布局 *!*/
/*  .box-image {*/
/*    width: 40vw;*/
/*    height: 40vw;*/
/*  }*/
/*}*/



/* 火箭 */
.scene{
  position: relative;
  width: 100%;
  height: 100vh;
  background: linear-gradient(#2b1055, #7597de);
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  border-radius: 20px;
}
.scene i{
  position: absolute;
  top: -250px;
  background: rgba(255,255,255,0.5);
  animation: star-run  linear infinite;
}
@keyframes star-run{
  0%{
    transform: translateY(0);
  }
  100%{
    transform: translateY(200vh);
  }
}
.scene .rocket{
  position: relative;
  animation: rocket-move 0.25s ease infinite;
}
@keyframes rocket-move{
  0%,100%{
    transform: translateY(-2px);
  }
  50%{
    transform: translateY(2px);
  }
}
.scene .rocket::before{
  content: '';
  width: 10px;
  height: 200px;
  background-image: linear-gradient(#00d0ff,transparent);
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  bottom: -200px;
  filter: blur(5px);
}
.scene .rocket::after{
  content: '';
  width: 10px;
  height: 200px;
  background-image: linear-gradient(#00d0ff,transparent);
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  bottom: -200px;
  filter: blur(15px);
}


</style>
