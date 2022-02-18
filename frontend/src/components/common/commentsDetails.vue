<template>
  <el-container style="margin: 0 0 60px;">
    <el-main class="out-box-white">
      <el-container style="margin-bottom: 20px; margin-left: 10px">
        <h2 style="text-align: left">评论详情</h2>
      </el-container>

      <el-col :span="16">
        <el-card class="review-card" style="border-radius: 10px; margin: 0 2% 20px 2%">
          <div style="margin-top: 20px; margin-bottom: 80px">
            <el-col :span="16" style="text-align: left">
              <span style="margin-left: 30px; margin-right: 40px"><b>{{ reviewDetail.review.username }}</b></span>
              <span style="text-align: left; color: #999">{{ reviewDetail.review.time }}</span>
            </el-col>
            <el-col :span="8" style="text-align: right">
              <el-rate v-model="reviewDetail.review.rating" disabled show-score :colors="starColors" :text-color="starColors"
                       text-color="#ff9900"
              v-if="(reviewDetail.review.delete_flag === false && reviewDetail.review.hide_flag === false) || (authority.indexOf('Librarian') > -1 && reviewDetail.review.hide_flag === true && reviewDetail.review.delete_flag === false)"/>
            </el-col>
          </div>
          <div style="padding-left: 3%; padding-right: 3%; text-align: left; text-indent: 2em; line-height: 28px"
               v-if="reviewDetail.review.delete_flag === false && reviewDetail.review.hide_flag === false">
            {{ reviewDetail.review.review }}
          </div>
          <div style="padding-left: 3%; padding-right: 3%; text-align: left; text-indent: 2em; line-height: 28px"
               v-if="reviewDetail.review.delete_flag === false && (reviewDetail.review.hide_flag === true && authority.indexOf('Librarian') > -1)">
            {{ reviewDetail.review.review }}
            <span style="color: red; font-style: italic">(处于隐藏状态)</span>
          </div>
          <div style="padding-left: 3%; padding-right: 3%; text-align: left; text-indent: 2em; line-height: 28px;"
               v-if="reviewDetail.review.delete_flag === true">
            <span style="color: #999; font-style: italic">该评论已被发布者删除</span>
          </div>
          <div style="padding-left: 3%; padding-right: 3%; text-align: left; text-indent: 2em; line-height: 28px;"
               v-if="reviewDetail.review.hide_flag === true && authority.indexOf('Reader') > -1">
            <span style="color: #999; font-style: italic">该评论已被管理员隐藏</span>
          </div>
          <div style="height: 30px"/>
          <div style="padding-left: 3%; padding-right: 3%; text-align: left">
            <el-button @click="dialogCommentVisible = true, currentReviewId = reviewDetail.review.id" type="text" v-if="authority.indexOf('Reader') > -1 && reviewDetail.review.hide_flag === false && reviewDetail.review.delete_flag === false">点击回复</el-button>
            <el-button @click="deleteRating(reviewDetail.review.id)" type="text"
                       v-if="authority.indexOf('Reader') > -1 && reviewDetail.review.username === username && reviewDetail.review.delete_flag === false" style="color: red">
              删除评论
            </el-button>
            <div style="text-align: right">
              <el-button @click="hideRating(reviewDetail.review.id)" type="text"
                         v-if="authority.indexOf('Librarian') > -1 && reviewDetail.review.hide_flag === false && reviewDetail.review.delete_flag === false" style="color: red">
                隐藏评论
              </el-button>
              <el-button @click="showRating(reviewDetail.review.id)" type="text"
                       v-if="authority.indexOf('Librarian') > -1 && reviewDetail.review.hide_flag === true && reviewDetail.review.delete_flag === false">
                取消隐藏
              </el-button>
            </div>
          </div>


          <el-divider/>
          <div style="font-size: 14px" v-for="comment in this.reviewDetail.comments" v-if="reviewDetail.comments.length > 0"> <!-- V-FOR HERE -->
            <el-col :span="3">&nbsp;</el-col>
            <el-col :span="21">
              <div style="margin-top: 10px; margin-bottom: 40px;">
                <el-col :span="16" style="text-align: left">
                  <b>{{ comment.username }}</b>
                </el-col>
                <el-col :span="8" style="text-align: right; color: #999">
                  {{ comment.time }}
                </el-col>
              </div>
              <div style="height: 10px"/>
              <div style="padding-left: 3%; padding-right: 3%; text-align: left; text-indent: 2em; line-height: 28px" v-if="comment.delete_flag === false && comment.hide_flag === false">
                {{ comment.comment }}
              </div>
              <div style="padding-left: 3%; padding-right: 3%; text-align: left; text-indent: 2em; line-height: 28px;" v-if="comment.delete_flag === true">
                <span style="color: #999; font-style: italic">该回复已被发布者删除</span>
                <div style="height: 20px"/>
              </div>
              <div style="padding-left: 3%; padding-right: 3%; text-align: left; text-indent: 2em; line-height: 28px"
                   v-if="comment.hide_flag === true && authority.indexOf('Librarian') > -1">
                {{ reviewDetail.review.review }}
                <span style="color: red; font-style: italic">(处于隐藏状态)</span>
              </div>
              <div style="padding-left: 3%; padding-right: 3%; text-align: left; text-indent: 2em; line-height: 28px;"
                   v-if="comment.hide_flag === true && authority.indexOf('Reader') > -1">
                <span style="color: #999; font-style: italic">该回复已被管理员隐藏</span>
                <div style="height: 20px"/>
              </div>
              <div style="padding-right: 3%; text-align: right;">
                <el-button @click="deleteComment(comment.id)" type="text" v-if="authority.indexOf('Reader') > -1 && username === comment.username && comment.delete_flag === false"
                           style="color: red">删除回复
                </el-button>
                <el-button @click="hideComment(comment.id)" type="text" v-if="authority.indexOf('Librarian') > -1 && comment.hide_flag === false && comment.delete_flag === false"
                           style="color: red">隐藏回复
                </el-button>
                <el-button @click="showComment(comment.id)" type="text" v-if="authority.indexOf('Librarian') > -1 && comment.hide_flag === true && comment.delete_flag === false">取消隐藏</el-button>
              </div>
              <el-divider/>
            </el-col>
          </div>
          <div style="font-size: 14px" v-if="reviewDetail.comments.length === 0">无回复</div>
        </el-card>
      </el-col>

      <el-col :span="8" style="padding-left: 20px">
        <el-image style="width: 100%; height: 100%; border-radius: 20px"
            :src="'data:image/jpeg;base64,'+ book.image"
            :preview-src-list="['data:image/jpeg;base64,'+ book.image]">
          <div slot="placeholder" class="image-slot">加载中<span class="dot">...</span></div>
        </el-image>
      </el-col>

      <el-dialog title="回复" :visible.sync="dialogCommentVisible">
        <el-form :model="addCommentForm">
          <el-form-item label="回复该评论" prop="comment">
            <el-input v-model="addCommentForm.comment" type="textarea" :autosize="{ minRows: 6, maxRows: 10}"
                      placeholder="对这篇评论发表一些看法吧！"/>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogCommentVisible = false">取 消</el-button>
          <el-button type="primary" @click="addComment(currentReviewId)">确 定</el-button>
        </div>
      </el-dialog>

    </el-main>
  </el-container>
</template>

<script>
import axios from "axios";

export default {
  name: "commentsDetails",
  data() {
    return {
      authority: localStorage.getItem('authority') || ['null'],
      scoreValue: 4.5,
      starColors: ['#99A9BF', '#F7BA2A', '#FF9900'],
      username: localStorage.getItem('userDetails'),
      reviewDetail: {
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
      },
      book: {
        name: 'a',
        author: 'A',
        date: '2020-1-1',
        isbn: '1',
        introduction: 'One',
        image: require('../../assets/logo.png'),
        count: 0
      },
      dialogCommentVisible: false,
      addCommentForm: {comment: ''},
      currentReviewId:'',
    }
  },

  methods: {
    deleteRating: function (reviewId) {
      this.$confirm('此操作将永久删除该评论，删除后将无法恢复且无法再对该书评论， 是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let formData = new FormData()
        formData.append('username', this.username)
        formData.append('reviewID', reviewId)
        axios({
          method: 'delete',
          url: 'http://139.196.174.46:8081/deleteReview',
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          withCredentials: true,
          data: formData
        })
          .then(resp => {
            if (resp.status === 200) {
              this.$message.success('评价删除成功')
              setTimeout(function () {
                location.reload()
              }, 2000)
            } else {
              this.$message.error('评价删除失败')
            }
          })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    hideRating(reviewId) {
      let _this = this
      axios.get('http://139.196.174.46:8081/hideReview/' + reviewId).then(function (resp) {
        if (resp.status === 200){
          _this.$message.success('评论已隐藏')
          setTimeout(function () {
            location.reload()
          }, 2000)
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
        if (resp.status === 200){
          _this.$message.success('评论已取消隐藏')
          setTimeout(function () {
            location.reload()
          }, 2000)
        } else {
          _this.$message.error('评论取消隐藏失败')
        }
      })
        .catch(error => {
          this.$message.error(error.response.data.message)
        })
    },
    addComment(reviewId) {
      if (this.addCommentForm.comment === null || this.addCommentForm.comment === '' || /^[\s]*$/.test(this.addCommentForm.comment)){
        this.$message.error('回复不能为空')
      } else {
        let formData = new FormData()
        formData.append('username', this.username)
        formData.append('reviewID', reviewId)
        formData.append('comment', this.addCommentForm.comment)
        axios({
          method: 'post',
          url: 'http://139.196.174.46:8081/publishComment',
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          withCredentials: true,
          data: formData
        })
          .then(resp => {
            if (resp.status === 200) {
              this.$message.success('回复成功')
              this.dialogCommentVisible = false
              setTimeout(function () {
                location.reload()
              }, 2000)
            } else {
              this.$message.error('回复失败')
            }
          })
          .catch(error => {
            this.$message.error(error.response.data.message)
          })
      }
    },
    deleteComment(commentId) {
      this.$confirm('此操作将永久删除该回复，是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let formData = new FormData()
        formData.append('username', this.username)
        formData.append('commentID', commentId)
        axios({
          method: 'delete',
          url: 'http://139.196.174.46:8081/deleteComment',
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          withCredentials: true,
          data: formData
        })
          .then(resp => {
            if (resp.status === 200) {
              this.$message.success('回复删除成功')
              setTimeout(function () {
                location.reload()
              }, 2000)
            } else {
              this.$message.error('回复删除失败')
            }
          })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    hideComment(commentId) {
      let _this = this
      axios.get('http://139.196.174.46:8081/hideComment/' + commentId).then(function (resp) {
        if (resp.status === 200){
          _this.$message.success('回复已隐藏')
          setTimeout(function () {
            location.reload()
          }, 2000)
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
        if (resp.status === 200){
          _this.$message.success('回复已取消隐藏')
          setTimeout(function () {
            location.reload()
          }, 2000)
        } else {
          _this.$message.error('回复取消隐藏失败')
        }
      })
        .catch(error => {
          this.$message.error(error.response.data.message)
        })
    }
  },
  created() {
    let _this = this
    let isbn, reviewDelete, reviewHide
    axios.get('http://139.196.174.46:8081/getReviewDetails/' + _this.$route.query.reviewId).then(function (resp) {
      _this.reviewDetail = resp.data
      isbn = resp.data.review.isbn
      reviewDelete = resp.data.review.delete_flag
      reviewHide = resp.data.review.hide_flag
      // if (reviewHide && _this.authority.indexOf('Reader') > -1){
      //   _this.$message.error('该评论已被管理员隐藏，不能查看详情')
      //   _this.$router.push({path: 'details', query: {isbn: isbn}})
      // }
      axios.get('http://139.196.174.46:8081/findBookByIsbn/' + isbn).then(function (resp) {
        _this.book = resp.data
      })
    })
      .catch(error => {
        this.$message.error('该评论不存在或已被用户删除')
        this.$router.replace('/')
      })
  }
}
</script>

<style scoped>

</style>
