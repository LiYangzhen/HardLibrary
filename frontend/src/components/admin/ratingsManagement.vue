<template>
  <el-container style="margin: 0 0 60px;">
    <el-header
      style="height: 80px; padding-top: 10px; padding-bottom: 10px; background-color: rgb(56,141,220); border-radius: 20px 20px 0 0;">
      <el-form ref="form" :model="form">
        <el-input placeholder="请输入内容" v-model="form.input" class="input-with-select">
          <el-select v-model="form.select" slot="prepend" placeholder="请选择">
            <el-option label="按用户搜索" value="username"/>
            <el-option label="按图书ISBN搜索" value="isbn"/>
          </el-select>
          <el-button slot="append" icon="el-icon-search" @click="submitForm(form)">搜索</el-button>
        </el-input>
      </el-form>
    </el-header>

    <el-main v-if="!judgeCommentOrReview.ifComment && !judgeCommentOrReview.ifReview && !show.copyHistory"
             class="out-box" style="background-color: #fdfdfd;">
      <div style="margin: 100px; font-size: 1.5em; color: #577485"><b>【书评管理】&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;请在上方横框中输入内容后，点击
        <i class="el-icon-search"/> 搜索</b></div>
    </el-main>

    <el-main v-if="judgeCommentOrReview.ifComment || judgeCommentOrReview.ifReview" class="out-box"
             style="background-color: #fdfdfd;">
      <h3 style="text-align: left">用户 {{ this.input }} 的评价</h3>
      <el-tabs type="border-card" value="first" style="border-radius: 5px">
        <el-tab-pane label="评论记录" name="first">
          <el-table max-height="400" stripe
                    style="width: 95%; margin: auto; border: 1px solid #ccc; border-radius: 10px;"
                    :data="this.reviews">
            <!--            <template slot-scope="props" v-if="props.row.reviewStatus !== '未评价'">-->
            <el-table-column label="图书编号" prop="isbn" sortable width="200"/>
            <el-table-column label="书名" prop="name" sortable width="200"/>
            <el-table-column label="作者" prop="author" sortable width="150"/>
            <el-table-column label="评价时间" prop="review.time" sortable width="200"/>
            <el-table-column label="评价评分" prop="review.rating" sortable width="100"/>
            <el-table-column label="评论内容" prop="review.review" sortable width="500" :show-overflow-tooltip='true'/>
            <el-table-column label="评价状态" prop="reviewStatus" sortable fixed="right" width="100"/>
            <el-table-column label="操作" fixed="right" width="100">
              <template slot-scope="props">
                <el-button @click="hideRating(props.row.review.id)" type="text" style="color: red"
                           v-if="props.row.reviewStatus === '已评价' && props.row.review.hide_flag === false">隐藏
                </el-button>
                <el-button @click="showRating(props.row.review.id)" type="text"
                           v-if="props.row.reviewStatus === '已隐藏' && props.row.review.hide_flag === true">取消隐藏
                </el-button>
              </template>
            </el-table-column>
            <el-table-column label="详情" fixed="right" width="100">
              <template slot-scope="props">
                <el-button @click="toCommentsDetails(props.row.review.id)" type="text">评论详情</el-button>
              </template>
            </el-table-column>
            <!--            </template>-->
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="回复记录" name="second">
          <el-table max-height="400" stripe
                    style="width: 95%; margin: auto; border: 1px solid #ccc; border-radius: 10px;"
                    :data="this.comments">
            <el-table-column label="图书编号" prop="review.isbn" sortable width="200"/>
            <el-table-column label="书名" prop="name" sortable width="200"/>
            <el-table-column label="作者" prop="author" sortable width="150"/>
            <el-table-column label="回复时间" prop="comment.time" sortable width="200"/>
            <el-table-column label="回复内容" prop="comment.comment" sortable width="400" :show-overflow-tooltip='true'/>
            <el-table-column label="回复状态" sortable fixed="right" width="100">
              <template slot-scope="props">
                <div v-if="props.row.status === '已评价'">正常</div>
                <div v-else>{{ props.row.status }}</div>
              </template>
            </el-table-column>
            <el-table-column label="评论内容" prop="review.review" sortable width="300" :show-overflow-tooltip='true'/>
            <el-table-column label="操作" fixed="right" width="100">
              <template slot-scope="props">
                <el-button @click="hideComment(props.row.comment.id)" type="text" style="color: red"
                           v-if="props.row.status === '已评价' && props.row.comment.hide_flag === false">隐藏
                </el-button>
                <el-button @click="showComment(props.row.comment.id)" type="text"
                           v-if="props.row.status === '已隐藏' && props.row.comment.hide_flag === true">取消隐藏
                </el-button>
              </template>
            </el-table-column>
            <el-table-column label="详情" fixed="right" width="100">
              <template slot-scope="props">
                <el-button @click="toCommentsDetails(props.row.review.id)" type="text">评论详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-main>

    <el-main v-if="show.copyHistory" class="out-box" style="background-color: #fdfdfd;">
      <el-card class="box-card">
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
                <el-rate v-model="book.rating" disabled show-score :colors="starColors" :text-color="starColors"
                         text-color="#ff9900" style="margin-top: 10px"/>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
      </el-card>
      <el-tabs type="border-card" value="first" style="border-radius: 5px">
        <el-tab-pane label="评论记录" name="first">
          <el-table max-height="400" stripe
                    style="width: 95%; margin: auto; border: 1px solid #ccc; border-radius: 10px;"
                    :data="this.bookReviews">
            <el-table-column label="发布者用户名" prop="review.username" sortable width="150"/>
            <el-table-column label="评价时间" prop="review.time" sortable width="200"/>
            <el-table-column label="评价评分" prop="review.rating" sortable width="100"/>
            <el-table-column label="评论内容" prop="review.review" sortable width="500" :show-overflow-tooltip='true'/>
            <el-table-column label="评价状态" prop="reviewStatus" sortable fixed="right" width="100">
              <template slot-scope="props">
                <div v-if="props.row.status === '已隐藏'">已隐藏</div>
                <div v-if="props.row.status === '已删除'">已删除</div>
                <div v-if="props.row.status === '已评价'">已评价</div>
              </template>
            </el-table-column>
            <el-table-column label="操作" prop="" fixed="right" width="100">
              <template slot-scope="props">
                <el-button @click="hideRating(props.row.review.id)" type="text" style="color: red"
                           v-if="props.row.status === '已评价'">隐藏
                </el-button>
                <el-button @click="showRating(props.row.review.id)" type="text" v-if="props.row.status === '已隐藏'">
                  取消隐藏
                </el-button>
              </template>
            </el-table-column>
            <el-table-column label="详情" fixed="right" width="100">
              <template slot-scope="props">
                <el-button @click="toCommentsDetails(props.row.review.id)" type="text">评论详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="回复记录" name="second">
          <el-table max-height="400" stripe
                    style="width: 95%; margin: auto; border: 1px solid #ccc; border-radius: 10px;"
                    :data="this.bookComments">
            <el-table-column label="回复用户" prop="username" sortable width="150"/>
            <el-table-column label="回复时间" prop="time" sortable width="200"/>
            <el-table-column label="回复内容" prop="comment" sortable width="400" :show-overflow-tooltip='true'/>
            <el-table-column label="回复状态" sortable fixed="right" width="100">
              <template slot-scope="props">
                <div v-if="props.row.hide_flag === false && props.row.delete_flag === false">正常</div>
                <div v-if="props.row.hide_flag === true && props.row.delete_flag === false">已隐藏</div>
                <div v-if="props.row.delete_flag === true">已删除</div>
              </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" width="100">
              <template slot-scope="props">
                <el-button @click="hideComment(props.row.id)" type="text" style="color: red"
                           v-if="props.row.hide_flag === false && props.row.delete_flag === false">隐藏
                </el-button>
                <el-button @click="showComment(props.row.id)" type="text"
                           v-if="props.row.hide_flag === true && props.row.delete_flag === false">取消隐藏
                </el-button>
              </template>
            </el-table-column>
            <el-table-column label="详情" fixed="right" width="100">
              <template slot-scope="props">
                <el-button @click="toCommentsDetails(props.row.reviewId)" type="text">评论详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-main>

  </el-container>
</template>

<script>

import axios from 'axios'

export default {
  name: 'ratingsManagement',

  data() {
    return {
      authority: localStorage.getItem('authority') || ['null'],
      form: {
        input: '',
        select: 'username'
      },
      show: {
        userHistory: false,
        copyHistory: false,
      },
      userHistory: false,
      copyHistory: false,
      input: '',
      reviews: [{
        review: {
          id: '',
          isbn: '',
          username: '',
          review: '',
          rating: '',
          time: '',
          delete_flag: '',
          hide_flag: '',
        },
        comments: [{
          comment: '',
          delete_flag: '',
          hide_flag: '',
          id: '',
          reviewId: '',
          time: '',
          username: ''
        }]
      }],
      bookReviews: [],
      book: {
        name: 'a',
        author: 'A',
        date: '2020-1-1',
        isbn: '1',
        introduction: 'One',
        image: require('../../assets/logo.png'),
        count: 0
      },
      comments: [],
      judgeCommentOrReview: {
        ifComment: false,
        ifReview: false
      },
      starColors: ['#99A9BF', '#F7BA2A', '#FF9900'],
      bookComments: []
    }
  },

  methods: {
    submitForm() {
      let _this = this
      //根据不同搜索内容，调用不同接口
      if (this.form.select === 'username') {
        axios.get('http://139.196.174.46:8081/getUserReviews/' + _this.form.input).then(function (resp) {
          if (resp.data.length !== 0) {
            _this.input = _this.form.input
            // _this.reviews = resp.data
            let count = 0
            for (let i = 0; i < resp.data.length; i++) {
              if (resp.data[i].reviewStatus !== '待评价') {
                _this.$set(_this.reviews, count, resp.data[i])
                count++
              }
            }
            _this.$set(_this.judgeCommentOrReview, 'ifReview', true)
          }
          //没有评论记录，给出相应提示
          else {
            _this.$message.warning('该用户没有评论')
            _this.input = null
            _this.$set(_this.judgeCommentOrReview, 'ifReview', false)
            _this.reviews = null
          }
        })
          .catch(error => {
            this.$message.error(error.response.data.message)
            _this.input = null
            _this.$set(_this.judgeCommentOrReview, 'ifReview', false)
            _this.reviews = null
          })
        axios.get('http://139.196.174.46:8081/getUserComments/' + _this.form.input).then(function (resp) {
          if (resp.data.length !== 0) {
            _this.comments = resp.data
            _this.$set(_this.judgeCommentOrReview, 'ifComment', true)
          }
          //没有回复记录，给出相应提示
          else {
            _this.$message.warning('该用户没有回复')
            _this.input = null
            _this.$set(_this.judgeCommentOrReview, 'ifComment', false)
            _this.comments = null
          }
        })
          .catch(error => {
            // this.$message.error(error.response.data.message)
            _this.input = null
            _this.$set(_this.judgeCommentOrReview, 'ifComment', false)
            _this.comments = null
          })
        _this.$set(_this.show, 'copyHistory', false)
      } else if (this.form.select === 'isbn') {
        _this.$set(_this.judgeCommentOrReview, 'ifReview', false)
        _this.$set(_this.judgeCommentOrReview, 'ifComment', false)
        axios.get('http://139.196.174.46:8081/findBookByIsbn/' + _this.form.input).then(function (resp) {
          _this.book = resp.data
        })
          .catch(error => {
            // this.$message.error(error.response.data.message)
          })
        axios.get('http://139.196.174.46:8081/getBookReviews/' + _this.form.input).then(function (resp) {
          if (resp.data.length !== 0) {
            // _this.userHistory = false
            // _this.copyHistory = true
            _this.$set(_this.show, 'copyHistory', true)
            _this.$set(_this.show, 'userHistory', false)
            _this.input = _this.form.input
            _this.bookReviews = resp.data
            let commentCount = 0
            for (let i = 0; i < resp.data.length; i++) {
              for (let j = 0; j < resp.data[i].comments.length; j++) {
                _this.bookComments[commentCount] = resp.data[i].comments[j]
                commentCount++
              }
            }
          }
          //没有评论记录，给出相应提示
          else {
            _this.$message.warning('该图书没有评论')
            // _this.userHistory = false
            // _this.copyHistory = false
            _this.$set(_this.show, 'copyHistory', false)
            _this.$set(_this.show, 'userHistory', false)
            _this.input = null
          }
        })
          .catch(error => {
            this.$message.error(error.response.data.message)
            // _this.userHistory = false
            // _this.copyHistory = false
            _this.$set(_this.show, 'copyHistory', false)
            _this.$set(_this.show, 'userHistory', false)
            _this.input = null
          })
      }
    },
    hideComment(commentId) {
      let _this = this
      axios.get('http://139.196.174.46:8081/hideComment/' + commentId).then(function (resp) {
        if (resp.status === 200) {
          _this.$message.success('回复已隐藏')
          // setTimeout(function () {
          //   location.reload()
          // }, 2000)
          axios.get('http://139.196.174.46:8081/getUserComments/' + _this.input).then(function (resp) {
            if (resp.data.length !== 0) {
              _this.comments = resp.data
            }
          })
          axios.get('http://139.196.174.46:8081/getBookReviews/' + _this.input).then(function (resp) {
            if (resp.data.length !== 0) {
              let commentCount = 0
              for (let i = 0; i < resp.data.length; i++) {
                for (let j = 0; j < resp.data[i].comments.length; j++) {
                  _this.$set(_this.bookComments, commentCount, resp.data[i].comments[j])
                  commentCount++
                }
              }
            }
          })
          console.log(_this.bookComments)
        } else {
          _this.$message.error('回复隐藏失败')
        }
      })
        .catch(error => {
          this.$message.error(error.response.data.message)
        })
    },
    showComment(commentId) {
      let _this = this
      axios.get('http://139.196.174.46:8081/showComment/' + commentId).then(function (resp) {
        if (resp.status === 200) {
          _this.$message.success('回复已取消隐藏')
          // setTimeout(function () {
          //   location.reload()
          // }, 2000)
          axios.get('http://139.196.174.46:8081/getUserComments/' + _this.input).then(function (resp) {
            if (resp.data.length !== 0) {
              _this.comments = resp.data
            }
          })
          axios.get('http://139.196.174.46:8081/getBookReviews/' + _this.input).then(function (resp) {
            if (resp.data.length !== 0) {
              let commentCount = 0
              for (let i = 0; i < resp.data.length; i++) {
                for (let j = 0; j < resp.data[i].comments.length; j++) {
                  _this.$set(_this.bookComments, commentCount, resp.data[i].comments[j])
                  commentCount++
                }
              }
            }
          })
        } else {
          _this.$message.error('回复取消隐藏失败')
        }
      })
        .catch(error => {
          this.$message.error(error.response.data.message)
        })
    },
    hideRating(reviewId) {
      let _this = this
      axios.get('http://139.196.174.46:8081/hideReview/' + reviewId).then(function (resp) {
        if (resp.status === 200) {
          _this.$message.success('评论已隐藏')
          // setTimeout(function () {
          //   location.reload()
          // }, 2000)
          axios.get('http://139.196.174.46:8081/getUserReviews/' + _this.input).then(function (resp) {
            if (resp.data.length !== 0) {
              let count = 0
              for (let i = 0; i < resp.data.length; i++) {
                if (resp.data[i].reviewStatus !== '待评价') {
                  _this.$set(_this.reviews, count, resp.data[i])
                  count++
                }
              }
            }
          })
          axios.get('http://139.196.174.46:8081/getBookReviews/' + _this.input).then(function (resp) {
            if (resp.data.length !== 0) {
              _this.bookReviews = resp.data
            }
          })
        } else {
          _this.$message.error('评论隐藏失败')
        }
      })
        .catch(error => {
          this.$message.error(error.response.data.message)
        })
    },
    showRating(reviewId) {
      let _this = this
      axios.get('http://139.196.174.46:8081/showReview/' + reviewId).then(function (resp) {
        if (resp.status === 200) {
          _this.$message.success('评论已取消隐藏')
          // setTimeout(function () {
          //   location.reload()
          // }, 2000)
          axios.get('http://139.196.174.46:8081/getUserReviews/' + _this.input).then(function (resp) {
            if (resp.data.length !== 0) {
              let count = 0
              for (let i = 0; i < resp.data.length; i++) {
                if (resp.data[i].reviewStatus !== '待评价') {
                  _this.$set(_this.reviews, count, resp.data[i])
                  count++
                }
              }
            }
          })
          axios.get('http://139.196.174.46:8081/getBookReviews/' + _this.input).then(function (resp) {
            if (resp.data.length !== 0) {
              _this.bookReviews = resp.data
            }
          })
        } else {
          _this.$message.error('评论取消隐藏失败')
        }
      })
        .catch(error => {
          this.$message.error(error.response.data.message)
        })
    },
    toCommentsDetails(reviewId) {
      this.$router.push({path: 'commentsDetails', query: {reviewId: reviewId}})
    },
    toDetails: function (isbn) {
      this.$router.push({path: 'details', query: {isbn: isbn}})
    },

  }

}

</script>
<style>
.el-select .el-input {
  width: 150px !important;
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
  box-shadow: 0 0 20px rgb(0, 0, 0);
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
