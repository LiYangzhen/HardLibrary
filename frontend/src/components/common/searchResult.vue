<template>
  <el-container style="margin: 0">

    <el-header style="height: 80px; padding-top: 10px; padding-bottom: 10px; background-color: rgb(56,141,220); border-radius: 20px 20px 0 0;">
      <el-form ref="form" :model="form">
        <el-input placeholder="请输入内容" v-model="form.input" class="input-with-select">
          <el-select v-model="form.select" slot="prepend" placeholder="请选择">
            <el-option label="图书名称" value="name"/>
            <el-option label="图书作者" value="author"/>
            <el-option label="ISBN" value="ISBN"/>
          </el-select>
          <el-button slot="append" icon="el-icon-search" @click="submitForm(form)"> 搜索</el-button>
        </el-input>
      </el-form>
    </el-header>

    <el-main v-if="!show" class="out-box" style="background-color: #fdfdfd;">
      <div v-if="!show" style="margin: 100px; font-size: 1.5em; color: #577485"><b>【查询书籍】&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;请在上方横框中输入内容后，点击 <i class="el-icon-search"/> 搜索</b></div>
    </el-main>

    <el-main v-if="show" class="out-box" style="background-color: #fdfdfd;">
      <el-card class="box-card" :key="book.index"
               v-for="book in this.books.slice(booksPerPage * ( cur - 1 ), booksPerPage * cur)">
        <el-row type="flex" class="row-bg">
          <el-col :span="6">
            <el-image class="search-img" :src="'data:image/jpeg;base64,'+ book.image" :fit="fit">
              <div slot="placeholder" class="image-slot">
                加载中<span class="dot">...</span>
              </div>
            </el-image>
          </el-col>
          <el-col :span="18" class="text item details" style="margin-left: 14%; text-align: left">
            <div class="name">
              <span class="search-details-title">图书名称：</span>{{ book.name }}
            </div>
            <div class="author">
              <span class="search-details-title">图书作者：</span>{{ book.author }}
            </div>
            <div class="ISBN">
              <span class="search-details-title">ISBN：</span>{{ book.isbn }}
            </div>
            <div class="introduction">
              <span v-if="book.introduction.length < 16">
                <span class="search-details-title">图书简介：</span>{{ book.introduction }}
              </span>
              <span v-else>
                <span class="search-details-title">图书简介：</span>{{ book.introduction.substr(0, 14) + "..." }}
              </span>
            </div>
            <el-row>
              <el-col :span="12">
                <el-button class="search-details-btn" type="text" @click="toDetails(book.isbn)">详情</el-button>
              </el-col>
              <el-col :span="12">
                <!-- ### WRITE YOUR CODE HERE ### -->
                <el-rate v-model="book.rating" disabled show-score :colors="starColors" :text-color="starColors" text-color="#ff9900" style="margin-top: 10px"/>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
      </el-card>
    </el-main>

    <el-footer v-if="show" style="background: transparent">
      <el-row class="page-bar">
        <div class="block">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :page-sizes="[4, 8, 12, 16]"
            :page-size="8"
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="books.length"
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
  name: 'searchResult',

  data() {
    return {
      fit: 'cover',
      form: {
        input: '',
        select: 'name'
      },
      books: [],
      show: false, //展示书籍部分是否显示
      booksPerPage: 8, // 一页展示的数量
      cur: 1, // 当前页码
      starColors: ['#99A9BF', '#F7BA2A', '#FF9900'],
    }
  },

  methods: {
    //跳转到书籍详情页
    toDetails: function (isbn) {
      this.$router.push({path: 'details', query: {isbn: isbn}})
    },
    //一页显示数量改变
    handleSizeChange: function (val) {
      if (val !== this.booksPerPage) {
        this.booksPerPage = val
      }
    },
    //翻页
    handleCurrentChange: function (val) {
      if (val !== this.cur) {
        this.cur = val
      }
    },
    submitForm() {
      let _this = this
      //根据不同搜索内容，调用不同接口
      if (this.form.select === 'name') {
        axios.get('http://139.196.174.46:8081/searchByName/' + _this.form.input).then(function (resp) {
          _this.books = resp.data
          //若没有符合的结果，不显示书记展示的表，并给出提示
          if (resp.data.length === 0) {
            _this.show = false
            _this.$message.warning('nothing found')
          } else {
            _this.show = true
            _this.all = Math.ceil(_this.books.length / _this.booksPerPage)
          }
        })
      } else if (this.form.select === 'author') {
        axios.get('http://139.196.174.46:8081/searchByAuthor/' + _this.form.input).then(function (resp) {
          _this.books = resp.data
          if (resp.data.length === 0) {
            _this.show = false
            _this.$message.warning('nothing found')
          } else {
            _this.show = true
            _this.all = Math.ceil(_this.books.length / _this.booksPerPage)
          }
        })
      } else if (this.form.select === 'ISBN') {
        axios.get('http://139.196.174.46:8081/searchByIsbn/' + _this.form.input).then(function (resp) {
          _this.books = resp.data
          if (resp.data.length === 0) {
            _this.show = false
            _this.$message.warning('nothing found')
          } else {
            _this.show = true
            _this.all = Math.ceil(_this.books.length / _this.booksPerPage)
          }
        })
      }
    }
  }
}

</script>

<style>

.input-with-select .el-input-group__prepend {
  background-color: #fff;
}

.page-bar {
  margin-top: 3em;
  margin-bottom: 4em;
}

.search-img {
  width: 150%;
  height: 205px;
  margin-left: -22%;
  margin-top: -22%;
  border-right: 7px solid rgb(161, 211, 243);
  border-radius: 20px;
}

.box-card {
  margin: 20px 20px;
  padding: 0;
  position: relative;
  width: 39vw;
  height: 200px;
  display: inline-block;
  box-shadow: 0 0 20px rgb(0,0,0);
  border-radius: 40px 0 40px 40px;
  transition: .5s;
}

.box-card:hover {
  transform: translateY(-8px);
}

.text {
  font-size: 0.9em;
}

.item {
  margin-bottom: 1em;
}

.details {
  color: #577485;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  text-overflow: ellipsis; /* 超出部分显示省略号 */
  overflow: hidden; /*超出部分隐藏*/
}

.search-details-title {
  font-weight: bold;
}

.search-details-btn {
  transition: .5s;
}

.search-details-btn:hover {
  transform: scale(1.1);
}

.name {
  margin-bottom: 0.6em;
  margin-top: 1.5em;
  -webkit-line-clamp: 1; /* 最多显示几行 */
  white-space: nowrap; /*规定段落中的文本不进行换行 */
}

.author {
  margin-bottom: 0.6em;
  -webkit-line-clamp: 1; /* 最多显示几行 */
  white-space: nowrap; /*规定段落中的文本不进行换行 */
}

.ISBN {
  margin-bottom: 0.6em;
  -webkit-line-clamp: 1; /* 最多显示几行 */
  white-space: nowrap; /*规定段落中的文本不进行换行 */
}

.introduction {
  margin-bottom: 0.6em;
  -webkit-line-clamp: 2; /* 最多显示几行 */
}


</style>
