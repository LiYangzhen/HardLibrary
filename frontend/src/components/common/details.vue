<template>
  <el-container style="margin: 0 0 60px;">
    <el-main class="out-box-white">
      <el-container style="margin-bottom: 20px; margin-left: 10px">
        <h2 style="text-align: left">图书详情</h2>
      </el-container>

      <el-row type="flex" justify="center" style="width: 100%; margin-top: 10px; margin-bottom: 40px" :gutter="20">
        <el-col :span="9" style="margin-left: 10%">
          <el-image style="width: 100%; height: 100%; border-radius: 20px"
                    :src="'data:image/jpeg;base64,'+ book.image"
                    :preview-src-list="['data:image/jpeg;base64,'+ book.image]" :fit="fit">
            <div slot="placeholder" class="image-slot">加载中<span class="dot">...</span></div>
          </el-image>
        </el-col>
        <el-col :span="15" style="margin-left: 5%">
          <ul class="informationBox">
            <li><span class="span-title">图书名称：</span><span class="span-content">{{ book.name }}</span></li>
            <li><span class="span-title">图书作者：</span><span class="span-content">{{ book.author }}</span></li>
            <li><span class="span-title">出版时间：</span><span class="span-content">{{ book.date }}</span></li>
            <li><span class="span-title">ISBN：</span><span class="span-content">{{ book.isbn }}</span></li>
            <li><span class="span-title">馆藏总数：</span><span class="span-content">{{ book.count }}</span></li>
            <li><span class="span-title">图书价格：</span><span class="span-content">{{ book.price }} 元</span></li>
            <li><span class="span-title">图书简介：</span><span class="span-content">{{ book.introduction }}</span></li>
            <li><span class="span-title">图书评分：</span>
              <span class="span-content">
                <el-rate v-model="book.rating" disabled show-score :colors="starColors" :text-color="starColors"
                         text-color="#ff9900" style="margin-top: 3px"/>
              </span>
            </li>
          </ul>
        </el-col>
      </el-row>

      <el-divider/>

      <el-row>
        <el-col :span="11">
          <div style="height: 80px">
            <el-row>
              <h2 style="display: inline-block; font-size: 24px; margin-right: 30px">各副本当前状态信息</h2>
              <el-button @click="clearFilter">清除筛选</el-button>
              <el-button type="primary" @click="addCopyDialogVisible = true"
                         v-if="authority.indexOf('Librarian') > -1">添加副本
              </el-button>
            </el-row>
          </div>
          <el-table ref="filterTable" max-height="400" stripe :data="tableData"
                    style="width: 100%; border: 1px solid #ccc; border-radius: 10px;">
            <el-table-column type="expand" v-if="authority.indexOf('Librarian') > -1">
              <template slot-scope="props">
                <el-form label-position="left" inberline class="demo-table-expand">
                  <el-form-item label="图书副本 ID:">
                    <span>{{ props.row.isbn }}</span>
                  </el-form-item>
                  <el-form-item label="图书所属地址:">
                    <span>{{ props.row.location }}</span>
                  </el-form-item>
                  <el-form-item label="预约/借阅者:">
                    <span>{{ props.row.borrower || '无' }}</span>
                  </el-form-item>
                  <el-form-item label="预约/借阅时间:">
                    <span>{{ props.row.time || '无' }}</span>
                  </el-form-item>
                </el-form>
              </template>
            </el-table-column>
            <el-table-column label="图书 ID" prop="isbn" width="180"/>
            <el-table-column label="所属地址" prop="location" width="100"/>
            <el-table-column label="当前状态:  " prop="status" width="100"
                             :filters="[{ text: '可借阅', value: '可借阅' }, { text: '已借阅', value: '已借阅' }, { text: '已遗失', value: '已遗失' },{ text: '已损坏', value: '已损坏' },{text: '已预约',value:'已预约'}]"
                             :filter-method="filterTag"
                             filter-placement="bottom-end">
              <template slot-scope="scope">
                <el-tag
                  effect="dark"
                  :type="getStatus(scope.row.status)"
                  disable-transitions>{{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作">
              <template slot-scope="scope">
                <el-button @click="reserve(scope.row.isbn)" type="text"
                           v-if="authority.indexOf('Reader') > -1">预约
                </el-button>
                <el-button @click="pushDialog(scope.row.isbn,scope.row.location)" type="text"
                           v-if="authority.indexOf('Librarian') > -1">编辑
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
        <el-col :span="1">&nbsp;</el-col>
        <el-col :span="12">
          <div style="height: 80px">
            <el-row>
              <h2 style="font-size: 24px">图书评价</h2>
            </el-row>
          </div>

          <el-card class="review-card" v-for="review in this.reviews" v-bind:key="reviews.id"
                   style="border-radius: 10px; margin-bottom: 20px"> <!-- V-FOR HERE -->
            <div style="margin-top: 10px; margin-bottom: 70px">
              <el-col :span="6" style="text-align: left">
                <b>{{ review.review.username }}</b>
              </el-col>
              <el-col :span="8" style="text-align: left; color: #999">
                {{ review.review.time }}
              </el-col>
              <el-col :span="3">&nbsp;</el-col>
              <el-col :span="7" style="text-align: right">
                <el-rate v-model="review.review.rating" disabled show-score :colors="starColors"
                         :text-color="starColors"
                         text-color="#ff9900"
                         v-if="(review.review.delete_flag === false && review.review.hide_flag === false) || (authority.indexOf('Librarian') > -1 && review.review.hide_flag === true && review.review.delete_flag === false)"/>
              </el-col>
            </div>
            <div style="padding-left: 3%; padding-right: 3%; text-align: left; text-indent: 2em; line-height: 28px"
                 v-if="review.review.delete_flag === false && review.review.hide_flag === false">
              {{ review.review.review }}
            </div>
            <div style="padding-left: 3%; padding-right: 3%; text-align: left; text-indent: 2em; line-height: 28px"
                 v-if="review.review.delete_flag === false && (review.review.hide_flag === true && authority.indexOf('Librarian') > -1)">
              {{ review.review.review }}
              <span style="color: red; font-style: italic">(处于隐藏状态)</span>
            </div>
            <div style="padding-left: 3%; padding-right: 3%; text-align: left; text-indent: 2em; line-height: 28px;"
                 v-if="review.review.delete_flag === true">
              <span style="color: #999; font-style: italic">该评论已被发布者删除</span>
            </div>
            <div style="padding-left: 3%; padding-right: 3%; text-align: left; text-indent: 2em; line-height: 28px;"
                 v-if="review.review.hide_flag === true && authority.indexOf('Reader') > -1">
              <span style="color: #999; font-style: italic">该评论已被管理员隐藏</span>
            </div>
            <div style="height: 20px"/>
            <div style="padding-left: 3%; padding-right: 3%; text-align: right;">
              <el-button @click="toCommentsDetails(review.review.id)" type="text">评论详情</el-button>
              <el-button @click="deleteRating(review.review.id)" type="text"
                         v-if="authority.indexOf('Reader') > -1 && review.review.username === username && review.review.delete_flag === false" style="color: red">
                删除
              </el-button>
              <el-button @click="hideRating(review.review.id)" type="text"
                         v-if="authority.indexOf('Librarian') > -1 && review.review.hide_flag === false && review.review.delete_flag === false" style="color: red">
                隐藏
              </el-button>
              <el-button @click="showRating(review.review.id)" type="text"
                         v-if="authority.indexOf('Librarian') > -1 && review.review.hide_flag === true">
                取消隐藏
              </el-button>
            </div>


            <el-divider v-if="review.comments.length > 0"/>
            <div v-if="review.comments.length === 0" style="height: 10px"/>
            <div style="font-size: 14px"
                 v-if="review.comments.length > 0">
              <el-col :span="3">&nbsp;</el-col>
              <el-col :span="21">
                <div style="margin-top: 10px; margin-bottom: 40px;">
                  <el-col :span="8" style="text-align: left">
                    <b>{{ review.comments[0].username }}</b>
                  </el-col>
                  <el-col :span="8">&nbsp;</el-col>
                  <el-col :span="8" style="text-align: right; color: #999">
                    {{ review.comments[0].time }}
                  </el-col>
                </div>
                <div style="padding-left: 3%; padding-right: 3%; text-align: left; text-indent: 2em; line-height: 28px" v-if="review.comments[0].delete_flag === false && review.comments[0].hide_flag === false">
                  {{ review.comments[0].comment }}
                </div>
                <div style="padding-left: 3%; padding-right: 3%; text-align: left; text-indent: 2em; line-height: 28px;" v-if="review.comments[0].delete_flag === true">
                  <span style="color: #999; font-style: italic">该回复已被发布者删除</span>
                </div>
                <div style="padding-left: 3%; padding-right: 3%; text-align: left; text-indent: 2em; line-height: 28px;" v-if="review.comments[0].hide_flag === true">
                  <span style="color: #999; font-style: italic">该回复已被管理员隐藏</span>
                </div>
                <el-divider>
                  <span style="color: #999" v-if="review.comments.length > 1">还有 {{ review.comments.length - 1 }} 条回复于详情页查看</span>
                </el-divider>
                <div style="height: 10px"/>
              </el-col>
            </div>

          </el-card>
        </el-col>

      </el-row>
    </el-main>


    <el-dialog title="编辑副本" :visible.sync="editCopyDialogVisible">
      <el-form :model="editCopyForm">
        <el-form-item label="副本ID">
          <span style="text-align: left">{{ editCopyForm.id }}</span>
        </el-form-item>
        <el-form-item label="副本所属地址选择" prop="location">
          <el-radio-group v-model="editCopyForm.radio">
            <el-radio-button label="邯郸图书馆"/>
            <el-radio-button label="江湾图书馆"/>
            <el-radio-button label="枫林图书馆"/>
            <el-radio-button label="张江图书馆"/>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="副本是否遗失" prop="ifLost">
          <el-radio-group v-model="editCopyForm.ifLost">
            <el-radio-button label="已遗失"/>
            <el-radio-button label="未遗失"/>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <el-button type="danger"
                 @click="deleteCopy(editCopyForm.id)"> 删除副本
      </el-button>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editCopyDialogVisible = false">取 消</el-button>
        <el-button type="primary"
                   @click="submitEditCopyForm(editCopyForm.id)">确 定
        </el-button>
      </div>
    </el-dialog>

    <el-dialog title="添加副本" :visible.sync="addCopyDialogVisible">
      <el-form :model="addCopyForm" :rules="addCopyFormRules" ref="addCopyForm">
        <el-form-item label="图书所属地址选择">
          <el-radio-group v-model="addCopyForm.radio">
            <el-radio-button label="邯郸图书馆" v-if="librarianLocation === '邯郸图书馆'"/>
            <el-radio-button label="邯郸图书馆" v-else disabled/>
            <el-radio-button label="江湾图书馆" v-if="librarianLocation === '江湾图书馆'"/>
            <el-radio-button label="江湾图书馆" v-else disabled/>
            <el-radio-button label="枫林图书馆" v-if="librarianLocation === '枫林图书馆'"/>
            <el-radio-button label="枫林图书馆" v-else disabled/>
            <el-radio-button label="张江图书馆" v-if="librarianLocation === '张江图书馆'"/>
            <el-radio-button label="张江图书馆" v-else disabled/>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="number">新增副本数量
          <el-input v-model="addCopyForm.number"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addCopyDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitAddCopyForm('addCopyForm')">确 定</el-button>
      </div>
    </el-dialog>

  </el-container>
</template>

<script>
import axios from 'axios'

export default {
  name: 'details',
  data() {
    //新增图书副本的数量必须为正整数
    const numValid = (rule, value, callback) => {
      if (!/^[1-9][0-9]*$/.test(value)) {
        callback(new Error('请输入正整数'))
      } else {
        callback()
      }
    }
    return {
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
      starColors: ['#99A9BF', '#F7BA2A', '#FF9900'],
      fit: 'cover',
      editCopyDialogVisible: false,
      addCopyDialogVisible: false,
      book: {
        name: 'a',
        author: 'A',
        date: '2020-1-1',
        isbn: '1',
        introduction: 'One',
        image: require('../../assets/logo.png'),
        count: 0
      },
      tableData: [],
      editCopyForm: {
        id: '',
        radio: '',
        ifLost: '未遗失'
      },
      addCopyForm: {
        radio: localStorage.getItem('librarianLocation') || ['null'],
        number: ''
      },
      addCopyFormRules: {
        number: [{required: true, message: '请输入新增副本数量', trigger: 'blur'}, {validator: numValid, trigger: 'blur'}]
      },
      authority: localStorage.getItem('authority') || ['null'],
      librarianLocation: localStorage.getItem('librarianLocation') || ['null'],
      username: localStorage.getItem('userDetails')
    }
  },

  methods: {
    toCommentsDetails(reviewId) {
      this.$router.push({path: 'commentsDetails', query: {reviewId: reviewId}})
    },
    deleteRating(reviewId) {
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
    //各状态不同的外观
    getStatus: function (status) {
      switch (status) {
        case '可借阅':
          return 'success'
        case '已借阅':
          return 'warning'
        case '已遗失':
        case '已损坏':
          return 'danger'
        case '已预约':
          return 'info'
      }
    },
    //用户的预约功能
    reserve(isbn) {
      let formData = new FormData()
      formData.append('username', localStorage.getItem('userDetails'))
      formData.append('isbn', isbn)
      axios({
        method: 'post',
        url: 'http://139.196.174.46:8081/reserveBook',
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        withCredentials: true,
        data: formData
      })
        .then(resp => {
          if (resp.status === 200) {
            this.$message.success('reserved successfully')
            //2秒后刷新页面，相应副本状态变化，不刷新无法显示
            setTimeout(function () {
              location.reload()
            }, 2000)
          } else {
            this.$message.error('error')
          }
        })
        .catch(error => {
          this.$message.error(error.response.data.message)
        })
    },
    //管理员的删除副本
    deleteCopy(isbn) {
      //弹出提示框
      this.$confirm('此操作将删除该副本, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let _this = this
        axios.delete('http://139.196.174.46:8081/deleteCopy/' + isbn).then(function (resp) {
          if (resp.status === 200) {
            _this.$message({
              type: 'success',
              message: '删除成功!'
            })
            _this.editCopyDialogVisible = false
            setTimeout(function () {
              location.reload()
            }, 2000)
          } else {
            _this.$message({
              type: 'error',
              message: '删除失败!'
            })
          }
        })
          .catch(error => {
            this.$message.error(error.response.data.message)
          })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    //状态的过滤器
    filterTag(value, row) {
      return row.status === value
    },
    //清除状态筛选
    clearFilter() {
      this.$refs.filterTable.clearFilter()
    },
    //弹出对话框
    pushDialog(id, location) {
      this.editCopyForm.id = id
      this.editCopyForm.radio = location
      this.editCopyDialogVisible = true
    },
    submitEditCopyForm(isbn) {
      let formData = new FormData()
      formData.append('isbn', isbn)
      formData.append('location', this.editCopyForm.radio)
      if (this.editCopyForm.ifLost === '已遗失')
        formData.append('ifLost', true)
      else
        formData.append('ifLost', false)
      axios({
        method: 'post',
        url: 'http://139.196.174.46:8081/updateCopy',
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        withCredentials: true,
        data: formData
      })
        .then(resp => {
          if (resp.status === 200) {
            this.$message.success('updated successfully')
            this.editCopyDialogVisible = false
            setTimeout(function () {
              location.reload()
            }, 2000)
          } else {
            this.$message.error('update error')
          }
        })
        .catch(error => {
          this.$message.error(error.response.data.message)
        })
    },
    submitAddCopyForm(formName) {
      this.$refs[formName].validate((valid) => {
          if (valid) {
            let formData = new FormData()
            formData.append('isbn', this.$route.query.isbn)
            formData.append('count', this.addCopyForm.number)
            formData.append('location', this.librarianLocation)
            axios({
              method: 'post',
              url: 'http://139.196.174.46:8081/addCopies',
              headers: {
                'Content-Type': 'multipart/form-data'
              },
              withCredentials: true,
              data: formData
            })
              .then(resp => {
                if (resp.status === 200) {
                  this.$message.success('successful added')
                  this.addCopyDialogVisible = false
                  setTimeout(function () {
                    location.reload()
                  }, 2000)
                } else {
                  this.$message.error('add error')
                }
              })
              .catch(error => {
                this.$message.error(error.response.data.message)
              })
          } else {
            this.$message.error('error submit!!')
            return false
          }
        }
      )
    }
  },
  //页面创建时，向后端请求图书数据
  created() {
    let _this = this
    this.tableData = null
    axios.get('http://139.196.174.46:8081/findBookByIsbn/' + _this.$route.query.isbn).then(function (resp) {
      _this.book = resp.data
    })
    //以下代码原意为，若副本不在馆内，将界面上显示的地址显示为'已借出'
    axios.get('http://139.196.174.46:8081/findCopiesByIsbn/' + _this.$route.query.isbn).then(function (resp) {
      _this.tableData = resp.data
    })
      .then(resp => {
        for (let i = 0; i < _this.tableData.length; i++) {
          if (_this.tableData[i].location == null) {
            //以下本为'已借出'
            _this.tableData[i].location = '无'
          }
        }
      })
    axios.get('http://139.196.174.46:8081/getBookReviews/' + _this.$route.query.isbn).then(function (resp) {
      _this.reviews = resp.data
    })
  }
}

</script>

<style scoped>

.informationBox {
  margin-left: 5%;
  margin-right: 5%;
  margin-top: 20px;
  text-align: left;
  color: #3f475a;
}

.informationBox > li {
  margin: 15px 0 15px;
  font-size: 1.1em;
  display: flex;
}

.informationBox .span-title {
  flex: 0 1 150px;
  font-weight: bold;
  font-size: 20px;
  font-family: 华文楷体;
}

.informationBox .span-content {
  flex: 8 1 100px;
  word-wrap: break-word;
  word-break: normal;
  font-family: 华文楷体;
}
</style>
