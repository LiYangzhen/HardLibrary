<template>
  <el-container style="margin: 0 0 60px;">
    <el-main class="out-box-white">
      <el-container style="margin-bottom: 20px; margin-left: 10px">
        <h2 style="text-align: left">图书评价</h2>
      </el-container>

      <h3 style="text-align: left">您的评价记录</h3>
      <el-table :data="this.reviewRecords" max-height="400" stripe @row-click="handleEdit"
                style="width: 95%; margin: auto; border: 1px solid #ccc; border-radius: 10px;">
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" inberline class="demo-table-expand">
              <el-form-item label="您对该书的评分:" v-if="props.row.review !== null">
                <span>{{ props.row.review.rating }}</span>
              </el-form-item>
              <el-form-item label="您对该书的评论:" v-if="props.row.review !== null">
                <span>{{ props.row.review.review }}</span>
              </el-form-item>
              <el-form-item label="尚未评价" v-if="props.row.review === null"/>
            </el-form>
          </template>
        </el-table-column>

        <el-table-column label="图书标识" prop="isbn" sortable/>
        <el-table-column label="书名" prop="name" sortable/>
        <el-table-column label="作者" prop="author" sortable/>
        <el-table-column label="归还时间" prop="returnTime" sortable/>
        <!-- ### WRITE YOUR CODE HERE ### -->
        <el-table-column label="评价状态" prop="reviewStatus" sortable/>
        <el-table-column label="操作">
          <template slot-scope="props">
            <el-button @click="dialogRateVisible = true" type="text" v-if="props.row.reviewStatus === '待评价'">评价</el-button>
            <el-button @click="deleteRating" type="text" v-if="props.row.reviewStatus === '已评价' || props.row.reviewStatus === '已隐藏'">删除</el-button>
          </template>
        </el-table-column>
        <el-table-column label="详情" fixed="right" width="100">
          <template slot-scope="props">
            <el-button @click="toCommentsDetails(props.row.review.id)" type="text" v-if="props.row.reviewStatus !== '待评价'">评论详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="height: 50px"/>

      <!-- ### WRITE YOUR CODE HERE ### -->
      <el-dialog title="新增图书评价" :visible.sync="dialogRateVisible">
        <el-form :model="addRatingForm" :rules="rules">
          <el-form-item label="对该图书评分" prop="rating">
            <el-rate v-model="addRatingForm.rating" :colors="starColors" show-text allow-half="true"
                     style="margin-top: 10px"/>
          </el-form-item>
          <el-form-item label="对该图书评论" prop="review">
            <el-input v-model="addRatingForm.review" type="textarea" :autosize="{ minRows: 6, maxRows: 10}"
                      placeholder="对这本书发表一些看法吧！"/>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogRateVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitAddRatingForm('addRatingForm')">确 定</el-button>
        </div>
      </el-dialog>

    </el-main>
  </el-container>
</template>

<script>
import axios from "axios";

export default {
  name: 'ratings',

  data() {
    const dataValid = (rule, value, callback) => {
      if (!value || /^[\s]*$/.test(value)) {
        return callback(new Error('Can\'t be empty'))
      }
      return callback()
    }
    return {
      dialogRateVisible: false,
      addRatingForm: {rating: '', review: ''},
      starColors: ['#99A9BF', '#F7BA2A', '#FF9900'],
      rules: {
        rating: [{required: true, message: '', trigger: 'blur'}, {validator: dataValid, trigger: 'blur'}],
        review: [{required: true, message: '', trigger: 'blur'}, {validator: dataValid, trigger: 'blur'}]
      },
      ratingBookIsbn: '',
      rowData:[],
      borrowRecords: [
        {
          name: '',
          author: '',
          copy: {
            borrower: '',
            isbn: '',
            location: '',
            status: '',
            time: '',
            time_limit: ''
          },
          record: {
            borrowTime: '',
            borrow_admin: '',
            borrow_location: '',
            borrower: '',
            id: '',
            isbn: '',
            reserveTime: '',
            returnTime: '',
            return_admin: '',
            return_location: '',
            status: ''
          }
        }
      ],
      reviewRecords:[
        {
          name:'',
          author:'',
          isbn:'',
          returnTime:'',
          reviewStatus:'',
          review: {
            id:'',
            isbn:'',
            username:'',
            review:'',
            rating:'',
            time:'',
            delete_flag:'',
            hide_flag:''
          }
        }
      ]
    }

  },

  methods: {
    deleteRating() {
      this.$confirm('此操作将永久删除该评论，删除后将无法恢复且无法再对该书评论， 是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {

        let formData = new FormData()
        formData.append('username', localStorage.getItem('userDetails'))
        formData.append('reviewID', this.rowData.review.id)
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

    submitAddRatingForm(formName) {
      if (this.addRatingForm.rating !== 0 && this.addRatingForm.review !== null && this.addRatingForm.review !== '' && !/^[\s]*$/.test(this.addRatingForm.review)) {
        let formData = new FormData()
        formData.append('username', localStorage.getItem('userDetails'))
        formData.append('isbn', this.ratingBookIsbn)
        formData.append('rating', this.addRatingForm.rating)
        formData.append('review', this.addRatingForm.review)
        axios({
          method: 'post',
          url: 'http://139.196.174.46:8081/publishReview',
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          withCredentials: true,
          data: formData
        })
          .then(resp => {
            if (resp.status === 200) {
              this.$message.success('评价发布成功')
              this.dialogRateVisible = false
              setTimeout(function () {
                location.reload()
              }, 2000)
            } else {
              this.$message.error('评价发布失败')
            }
          })
          .catch(error => {
            this.$message.error(error.response.data.message)
          })
      } else {
        this.$message.error('请填写评分及评论')
      }
    },
    handleEdit(row) {
      this.ratingBookIsbn = row.isbn
      this.rowData = row
    },
    toCommentsDetails(reviewId) {
      this.$router.push({path: 'commentsDetails', query: {reviewId: reviewId}})
    },
  },


  created() {
    let _this = this
    axios.get('http://139.196.174.46:8081/getUserRecords/' + localStorage.getItem('userDetails')).then(function (resp) {
      if (resp.data.length !== 0) {
        let j = 0
        for (let i = 0; i < resp.data.length; i++) {
          if (resp.data[i].record.returnTime !== null) {
            // 若同一图书（不同副本编号也算）被多次借阅，仅保留第一次
            let redundant = false
            for (let k = 0; k < _this.borrowRecords.length; k++) {
              // 副本编号截为图书编号
              if (_this.borrowRecords[k].copy.isbn.substr(0, 17) === resp.data[i].copy.isbn.substr(0, 17)) {
                redundant = true
                break
              }
            }
            if (!redundant) {
              _this.$set(_this.borrowRecords, j, resp.data[i])
              j++
            }
          }
        }
      } else {
        _this.$message.warning('no records to show')
      }
    })
      .catch(error => {
        this.$message.error(error.response.data.message)
      })
    axios.get('http://139.196.174.46:8081/getUserReviews/' + localStorage.getItem('userDetails')).then(function (resp) {
      _this.reviewRecords = resp.data
      // console.log(_this.reviewRecords)
    })
  },



}
</script>

<style scoped>

</style>
