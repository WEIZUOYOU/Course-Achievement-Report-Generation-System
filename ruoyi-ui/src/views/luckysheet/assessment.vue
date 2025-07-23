<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span style="font-size: 18px; font-weight: bold;">课程目标达成评价报告生成工具</span>
      </div>

      <!-- 步骤指示器 -->
      <el-steps :active="currentStep" align-center style="margin-bottom: 30px;">
        <el-step title="配置加载" description="加载课程配置" />
        <el-step title="数据录入" description="填写学生成绩" />
        <el-step title="数据处理" description="生成分析结果" />
        <el-step title="报告下载" description="下载评价报告" />
      </el-steps>

      <!-- 步骤1：配置加载 -->
      <div v-show="currentStep === 0">
        <el-row :gutter="20">
          <el-col :span="24">
            <h3>第一步：加载课程配置</h3>
            <p>系统将使用默认配置为您生成数据录入模板</p>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="loadDefaultConfig"
            >
              使用默认配置
            </el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 步骤2：Excel数据录入 -->
      <div v-show="currentStep === 1">
        <el-row :gutter="20">
          <el-col :span="24">
            <h3>第二步：Excel数据录入</h3>
            <p>使用在线Excel表格填写学生成绩数据，支持Excel的各种功能</p>
            
            <!-- Luckysheet容器 -->
            <div
              id="luckysheet"
              style="margin: 10px 0px; width: 100%; height: 1000px; border: 1px solid #ccc;"
            />

            <div style="margin-top: 20px;">
              <el-button
                type="primary"
                size="large"
                :loading="processing"
                @click="processData"
              >
                提交数据并生成报告
              </el-button>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 步骤3：数据处理 -->
      <div v-show="currentStep === 2">
        <el-row :gutter="20">
          <el-col :span="24">
            <h3>第三步：数据处理中</h3>
            <div style="text-align: center; padding: 50px;">
              <el-progress
                :percentage="processProgress"
                :status="processStatus || undefined"
                :stroke-width="8"
              />
              <p style="margin-top: 20px; font-size: 16px;">{{ processMessage }}</p>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 步骤4：结果展示和报告下载 -->
      <div v-show="currentStep === 3">
        <el-row :gutter="20">
          <el-col :span="24">
            <h3>第四步：结果展示与报告下载</h3>

            <!-- 统计图表展示 -->
            <el-tabs v-model="activeTab" type="card">
              <el-tab-pane label="成绩分布图" name="gradeDistribution">
                <div style="text-align: center;">
                  <img
                    v-if="results.gradeDistributionChart"
                    :src="getImageUrl(results.gradeDistributionChart)"
                    style="max-width: 100%; height: auto;"
                    alt="成绩分布图"
                    @error="handleImageError($event, results.gradeDistributionChart)"
                  >
                  <p v-else>暂无成绩分布图</p>
                </div>
              </el-tab-pane>

              <el-tab-pane label="达成度柱状图" name="achievementBar">
                <div style="text-align: center;">
                  <img
                    v-if="results.achievementBarChart"
                    :src="getImageUrl(results.achievementBarChart)"
                    style="max-width: 100%; height: auto;"
                    alt="达成度柱状图"
                    @error="handleImageError($event, results.achievementBarChart)"
                  >
                  <p v-else>暂无达成度柱状图</p>
                </div>
              </el-tab-pane>

              <el-tab-pane label="课程目标散点图" name="scatterCharts">
                <div style="text-align: center;">
                  <div v-if="results.scatterCharts && results.scatterCharts.length > 0">
                    <div
                      v-for="(chart, index) in results.scatterCharts"
                      :key="index"
                      style="margin-bottom: 20px;"
                    >
                      <h4>{{ chart.replace('_achievement_scatter_chart.png', '').replace('_', ' ') }}</h4>
                      <img
                        :src="getImageUrl(chart)"
                        style="max-width: 100%; height: auto;"
                        :alt="chart"
                        @error="handleImageError($event, chart)"
                      >
                    </div>
                  </div>
                  <p v-else>暂无课程目标散点图</p>
                </div>
              </el-tab-pane>
            </el-tabs>

            <!-- 下载按钮 -->
            <div style="margin-top: 30px; text-align: center;">
              <el-button
                type="success"
                size="large"
                icon="el-icon-download"
                @click="downloadReport"
              >
                下载完整报告 (Word格式)
              </el-button>

              <el-button
                type="primary"
                size="large"
                icon="el-icon-refresh"
                @click="restart"
              >
                重新开始
              </el-button>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script>
import { loadDefaultConfig, generateTemplate, processData } from '@/api/luckysheet/assessment'

export default {
  name: 'Assessment',
  data() {
    return {
      currentStep: 0,
      loading: false,
      processing: false,
      processProgress: 0,
      processStatus: null, // 修复：设置为null，避免空字符串导致的警告
      processMessage: '准备处理数据...',
      activeTab: 'gradeDistribution',
      configId: '',
      reportId: '',
      results: {},
      luckysheetInstance: null
    }
  },
  mounted() {
    // 设置页面标题
    document.title = '课程目标达成评价报告 - 若依管理系统'
    // 加载Luckysheet脚本
    this.loadLuckysheetScripts()
  },
  methods: {
    async loadDefaultConfig() {
      this.loading = true
      try {
        console.log('开始调用loadDefaultConfig API...')
        const response = await loadDefaultConfig()
        console.log('API完整响应:', response) // 添加调试日志
        console.log('response类型:', typeof response)
        console.log('response.configId:', response ? response.configId : 'response为undefined')
        
        // 检查响应是否存在
        if (!response) {
          console.error('API响应为空')
          this.$message.error('API响应为空，请检查网络连接')
          return
        }
        
        // 检查响应格式
        if (typeof response !== 'object') {
          console.error('API响应格式错误，期望对象，得到:', typeof response)
          this.$message.error('API响应格式错误')
          return
        }
        
        // 检查响应数据结构 - 修正：直接检查response而不是response.data
        if (response.code !== 200) {
          console.error('API返回错误:', response)
          this.$message.error('API调用失败: ' + (response.msg || '未知错误'))
          return
        }
        
        // 修正：直接访问response的属性，因为axios拦截器已经返回了res.data
        if (response && response.code === 200 && response.configId && response.config) {
          console.log('API调用成功，configId:', response.configId)
          this.configId = response.configId
          this.$message.success('配置加载成功')
          this.currentStep = 1
          this.$nextTick(() => {
            this.initLuckysheet(response.config)
          })
        } else {
          console.error('API响应数据不完整:', response)
          this.$message.error('API响应数据不完整，缺少必要字段')
        }
      } catch (error) {
        console.error('API调用完整错误:', error)
        console.error('错误类型:', error.constructor.name)
        console.error('错误消息:', error.message)
        console.error('错误堆栈:', error.stack)
        
        if (error.response) {
          console.error('HTTP错误响应:', error.response)
          console.error('HTTP状态码:', error.response.status)
          console.error('HTTP响应数据:', error.response.data)
        } else if (error.request) {
          console.error('网络请求错误:', error.request)
        }
        
        let errorMessage = '配置加载失败：'
        if (error.response) {
          // 服务器返回了错误响应
          errorMessage += `服务器错误 ${error.response.status}: ${JSON.stringify(error.response.data) || error.response.statusText}`
        } else if (error.request) {
          // 网络错误
          errorMessage += '网络连接失败，请检查前端服务器是否启动(端口80)和后端服务是否启动(端口8080)'
        } else if (error.message) {
          // 其他错误
          errorMessage += error.message
        } else {
          errorMessage += '未知错误，请查看控制台日志'
        }
        
        this.$message.error(errorMessage)
      } finally {
        this.loading = false
      }
    },

    async loadLuckysheetScripts() {
      try {
        // 动态加载Luckysheet的CSS文件
        const cssFiles = [
          '/luckysheet/css/luckysheet.css',
          '/luckysheet/assets/iconfont/iconfont.css',
          '/luckysheet/css/plugins/chart.css'
        ]
        
        for (const href of cssFiles) {
          const cssLink = document.createElement('link')
          cssLink.rel = 'stylesheet'
          cssLink.href = href
          document.head.appendChild(cssLink)
        }

        // 动态加载JS文件
        const scripts = [
          '/luckysheet/plugins/js/plugin.js',
          '/luckysheet/luckysheet.umd.js'
        ]

        for (const src of scripts) {
          await this.loadScript(src)
        }
        
        console.log('Luckysheet脚本加载完成')
      } catch (error) {
        console.error('Luckysheet脚本加载失败:', error)
      }
    },

    loadScript(src) {
      return new Promise((resolve, reject) => {
        const script = document.createElement('script')
        script.src = src
        script.onload = resolve
        script.onerror = reject
        document.head.appendChild(script)
      })
    },

    async initLuckysheet(config) {
      try {
        console.log('开始生成Excel模板...')
        // 生成Excel模板
        const templateResponse = await generateTemplate(this.configId)
        console.log('模板API完整响应:', templateResponse)
        
        if (templateResponse && templateResponse.code === 200) {
          // 修正：直接访问templateResponse.templateData，而不是templateResponse.data.templateData
          const templateData = templateResponse.templateData
          console.log('模板数据:', templateData)

          // 转换模板数据为Luckysheet格式
          const workbookData = this.convertToLuckysheetFormat(templateData)

          // 初始化Luckysheet
          if (window.luckysheet) {
            window.luckysheet.create({
              container: 'luckysheet',
              data: workbookData,
              title: '课程目标达成评价数据录入',
              lang: 'zh',
              allowCopy: true,
              allowUpdate: true,
              showConfigWindowResize: true,
              forceCalculation: false,
              // 启用完整功能
              showtoolbar: true,           // 显示工具栏
              showinfobar: true,           // 显示信息栏
              showsheetbar: true,          // 显示sheet标签栏
              showstatisticBar: true,      // 显示统计栏
              allowEdit: true,             // 允许编辑
              enableAddRow: true,          // 允许添加行
              enableAddCol: true,          // 允许添加列
              sheetFormulaBar: true,       // 显示公式栏
              showFormulaBar: true,        // 显示公式编辑栏
              // 工具栏配置
              showtoolbarConfig: {
                undoRedo: true,            // 撤销重做
                paintFormat: true,         // 格式刷
                currencyFormat: true,      // 货币格式
                percentageFormat: true,    // 百分比格式
                numberDecrease: true,      // 减少小数位
                numberIncrease: true,      // 增加小数位
                moreFormats: true,         // 更多格式
                font: true,                // 字体
                fontSize: true,            // 字体大小
                bold: true,                // 粗体
                italic: true,              // 斜体
                strikethrough: true,       // 删除线
                underline: true,           // 下划线
                textColor: true,           // 字体颜色
                fillColor: true,           // 背景颜色
                border: true,              // 边框
                mergeCell: true,           // 合并单元格
                horizontalAlignMode: true, // 水平对齐
                verticalAlignMode: true,   // 垂直对齐
                textWrapMode: true,        // 文本换行
                textRotateMode: true,      // 文本旋转
                image: true,               // 插入图片
                link: true,                // 插入链接
                chart: true,               // 图表
                postil: true,              // 批注
                pivotTable: true,          // 数据透视表
                function: true,            // 函数
                frozenMode: true,          // 冻结
                sortAndFilter: true,       // 排序和筛选
                conditionalFormat: true,   // 条件格式
                dataVerification: true,    // 数据验证
                splitColumn: true,         // 分列
                screenshot: true,          // 截图
                findAndReplace: true,      // 查找替换
                protection: true,          // 工作表保护
                print: true                // 打印
              }
            })
            this.luckysheetInstance = window.luckysheet
            console.log('Luckysheet初始化成功')
          } else {
            throw new Error('Luckysheet库未加载')
          }
        } else {
          throw new Error('模板生成失败: ' + (templateResponse ? templateResponse.msg : '未知错误'))
        }
      } catch (error) {
        console.error('初始化Excel详细错误:', error)
        this.$message.error('初始化Excel失败：' + error.message)
      }
    },

    convertToLuckysheetFormat(templateData) {
      const workbookData = []

      templateData.sheets.forEach((sheet, index) => {
        const celldata = []

        // 添加表头
        sheet.headers.forEach((header, colIndex) => {
          celldata.push({
            r: 0,
            c: colIndex,
            v: {
              v: header,
              ct: { fa: 'General', t: 's' },
              m: header,
              bg: '#f0f0f0',
              bl: 1
            }
          })
        })

        // 生成测试数据 - 根据不同sheet类型生成不同的数据
        const testData = this.generateTestData(sheet.name, sheet.headers)
        
        testData.forEach((rowData, rowIndex) => {
          const actualRowIndex = rowIndex + 1 // 跳过表头行
          rowData.forEach((cellValue, colIndex) => {
            celldata.push({
              r: actualRowIndex,
              c: colIndex,
              v: {
                v: cellValue,
                ct: { fa: 'General', t: typeof cellValue === 'number' ? 'n' : 's' },
                m: cellValue.toString()
              }
            })
          })
        })

        workbookData.push({
          name: sheet.name,
          index: index,
          status: index === 0 ? 1 : 0,
          order: index,
          celldata: celldata,
          config: {
            merge: {},
            rowlen: {},
            columnlen: {},
            rowhidden: {},
            colhidden: {},
            borderInfo: [],
            authority: {}
          },
          scrollLeft: 0,
          scrollTop: 0,
          luckysheet_select_save: [],
          calcChain: [],
          isPivotTable: false,
          pivotTable: {},
          filter_select: {},
          filter: null,
          luckysheet_alternateformat_save: [],
          luckysheet_alternateformat_save_modelCustom: [],
          luckysheet_conditionformat_save: {},
          frozen: {},
          chart: [],
          zoomRatio: 1,
          image: [],
          showGridLines: 1,
          dataVerification: {}
        })
      })

      return workbookData
    },

    // 生成测试数据
    generateTestData(sheetName, headers) {
      const testData = []
      
      // 学生基本信息
      const students = [
        { class: '计算机2021-1班', id: '20210101', name: '张三' },
        { class: '计算机2021-1班', id: '20210102', name: '李四' },
        { class: '计算机2021-1班', id: '20210103', name: '王五' },
        { class: '计算机2021-1班', id: '20210104', name: '赵六' },
        { class: '计算机2021-1班', id: '20210105', name: '陈七' },
        { class: '计算机2021-2班', id: '20210201', name: '刘八' },
        { class: '计算机2021-2班', id: '20210202', name: '周九' },
        { class: '计算机2021-2班', id: '20210203', name: '吴十' },
        { class: '计算机2021-2班', id: '20210204', name: '郑十一' },
        { class: '计算机2021-2班', id: '20210205', name: '孙十二' }
      ]

      if (sheetName.includes('期末考试')) {
        // 期末考试成绩数据
        students.forEach((student, index) => {
          const row = [student.class, student.id, student.name]
          
          // 为每个题目生成随机分数
          for (let i = 3; i < headers.length; i++) {
            const header = headers[i]
            // 从题目标题中提取分值
            const scoreMatch = header.match(/（(\d+(?:\.\d+)?)分）/)
            const maxScore = scoreMatch ? parseFloat(scoreMatch[1]) : 10
            
            // 生成0.7-0.95之间的得分率
            const scoreRate = 0.7 + Math.random() * 0.25
            const score = Math.round(maxScore * scoreRate * 10) / 10
            row.push(score)
          }
          testData.push(row)
        })
      } else if (sheetName.includes('平时成绩')) {
        // 平时成绩数据
        students.forEach((student, index) => {
          const baseScore = 75 + Math.random() * 20 // 75-95分之间
          const score = Math.round(baseScore)
          testData.push([student.class, student.id, student.name, score])
        })
      } else if (sheetName.includes('上机成绩')) {
        // 上机成绩数据
        students.forEach((student, index) => {
          const baseScore = 80 + Math.random() * 15 // 80-95分之间
          const score = Math.round(baseScore)
          testData.push([student.class, student.id, student.name, score])
        })
      }

      return testData
    },

    async processData() {
      this.processing = true
      this.currentStep = 2
      this.processProgress = 0
      this.processStatus = null
      this.processMessage = '正在提取Excel数据...'

      let progressTimer = null // 将声明移到这里

      try {
        // 获取Luckysheet数据
        const excelData = this.extractLuckysheetData()
        this.processProgress = 25
        this.processMessage = '正在处理数据，请耐心等待...'

        // 启动进度模拟器
        progressTimer = setInterval(() => {
          if (this.processProgress < 90) {
            this.processProgress += 1
            if (this.processProgress < 50) {
              this.processMessage = '正在分析成绩数据...'
            } else if (this.processProgress < 70) {
              this.processMessage = '正在计算课程目标达成度...'
            } else if (this.processProgress < 90) {
              this.processMessage = '正在生成图表和报告...'
            }
          }
        }, 2000)

        // 提交数据处理
        const response = await processData(excelData, this.configId)
        
        if (progressTimer) {
          clearInterval(progressTimer)
        }
        this.processProgress = 100
        this.processMessage = '数据处理完成！'

        console.log('processData API完整响应:', response)
        console.log('response.code:', response.code)
        console.log('response.results:', response.results)
        console.log('response.reportId:', response.reportId)

        if (response && response.code === 200) {
          this.results = response.results || {}
          this.reportId = response.reportId || ''
          this.processProgress = 100
          this.processStatus = 'success'
          this.processMessage = '数据处理完成！'

          setTimeout(() => {
            this.currentStep = 3
          }, 1000)
        } else {
          throw new Error('数据处理API返回错误: ' + (response.msg || '未知错误'))
        }
      } catch (error) {
        if (progressTimer) {  // 安全检查
          clearInterval(progressTimer)
        }
        this.processProgress = 100
        this.processStatus = 'exception'
        this.processMessage = '处理失败：' + error.message
        this.$message.error('数据处理失败：' + error.message)
      } finally {
        this.processing = false
      }
    },

    extractLuckysheetData() {
      const sheets = []

      if (this.luckysheetInstance && window.luckysheet.getAllSheets) {
        const allSheets = window.luckysheet.getAllSheets()
        console.log('提取的Sheet数据:', allSheets)

        allSheets.forEach(sheet => {
          const data = []
          const celldata = sheet.celldata || []

          console.log('处理Sheet:', sheet.name, '单元格数据:', celldata)

          // 确定数据范围
          let maxRow = 0
          let maxCol = 0
          celldata.forEach(cell => {
            maxRow = Math.max(maxRow, cell.r)
            maxCol = Math.max(maxCol, cell.c)
          })

          // 构建数据矩阵
          for (let r = 0; r <= maxRow; r++) {
            const row = []
            for (let c = 0; c <= maxCol; c++) {
              const cell = celldata.find(item => item.r === r && item.c === c)
              
              // 安全地提取单元格值
              let cellValue = ''
              if (cell && cell.v) {
                // 如果是对象格式的值
                if (typeof cell.v === 'object' && cell.v.v !== undefined) {
                  cellValue = cell.v.v
                } else {
                  // 如果是直接的值
                  cellValue = cell.v
                }
              }
              
              // 确保值不是null或undefined
              if (cellValue === null || cellValue === undefined) {
                cellValue = ''
              }
              
              row.push(cellValue)
            }
            data.push(row)
          }

          console.log('Sheet数据矩阵:', data)
          
          sheets.push({
            name: sheet.name,
            data: data
          })
        })
      } else {
        console.error('Luckysheet实例不可用')
        this.$message.error('无法获取Excel数据，请确保数据已正确录入')
        throw new Error('Luckysheet实例不可用')
      }

      console.log('最终提取的数据:', { sheets })
      return { sheets }
    },

    getImageUrl(fileName) {
      if (!fileName || !this.configId) {
        return null
      }
      
      // 确保configId不包含.json后缀用于URL路径
      let cleanConfigId = this.configId;
      if (cleanConfigId.endsWith('.json')) {
        cleanConfigId = cleanConfigId.substring(0, cleanConfigId.length - 5);
      }
      
      // 对文件名进行URL编码以处理中文字符
      const encodedFileName = encodeURIComponent(fileName);
      
      // 使用正确的API前缀，这样会通过代理转发到8080端口
      const imageUrl = `/dev-api/luckysheet/result/${cleanConfigId}/${encodedFileName}`;
      console.log('生成图片URL:', imageUrl);
      console.log('原始文件名:', fileName);
      console.log('编码后文件名:', encodedFileName);
      return imageUrl;
    },

    handleImageError(event, fileName) {
      console.warn('图片加载失败:', fileName, '错误:', event)
      console.log('尝试加载的URL:', event.target.src)
      
      // PNG加载失败，显示错误占位符
      event.target.style.display = 'none'
      const placeholder = event.target.parentNode.querySelector('.image-placeholder')
      if (!placeholder) {
        const div = document.createElement('div')
        div.className = 'image-placeholder'
        div.innerHTML = `
          <div style="
            display: flex; 
            flex-direction: column;
            align-items: center; 
            justify-content: center; 
            min-height: 300px;
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            border: 2px dashed #ddd; 
            border-radius: 8px;
            color: #666; 
            text-align: center;
            font-size: 14px;
            padding: 20px;
            margin: 10px 0;
          ">
            <div style="font-size: 48px; margin-bottom: 15px; color: #ff6b6b;">📊</div>
            <div style="font-size: 18px; font-weight: bold; margin-bottom: 10px; color: #333;">
              图片加载失败
            </div>
            <div style="font-size: 14px; color: #666; margin-bottom: 15px;">
              无法加载图片文件：${fileName}
            </div>
            <div style="font-size: 12px; color: #999; margin-bottom: 15px;">
              URL: ${event.target.src}
            </div>
            <div style="background: #fff3cd; border: 1px solid #ffeaa7; border-radius: 4px; padding: 15px; margin: 10px 0; text-align: left; max-width: 500px;">
              <div style="font-weight: bold; color: #856404; margin-bottom: 8px;">🔧 检查要点：</div>
              <div style="color: #856404; line-height: 1.6;">
                <div>• 确认后端服务正在运行 (localhost:8080)</div>
                <div>• 检查文件是否存在于服务器</div>
                <div>• 验证图片文件权限</div>
                <div>• 查看浏览器开发者工具的网络面板</div>
              </div>
            </div>
          </div>
        `
        event.target.parentNode.appendChild(div)
      }
    },

    async downloadReport() {
      try {
        // 使用正确的API前缀路径
        const downloadUrl = `/dev-api/luckysheet/download-report/${this.reportId}?configId=${this.configId}`
        console.log('下载URL:', downloadUrl)
        
        window.open(downloadUrl)
        this.$message.success('报告下载开始')
      } catch (error) {
        console.error('下载失败:', error)
        this.$message.error('下载失败：' + error.message)
      }
    },

    restart() {
      this.currentStep = 0
      this.configId = ''
      this.reportId = ''
      this.results = {}
      this.processProgress = 0
      this.processStatus = null // 重置为null
      this.processMessage = '准备处理数据...'

      // 清理Luckysheet实例
      if (document.getElementById('luckysheet')) {
        document.getElementById('luckysheet').innerHTML = ''
      }
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.box-card {
  margin-bottom: 20px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both;
}

h3 {
  color: #303133;
  margin-bottom: 15px;
}

p {
  color: #606266;
  margin-bottom: 20px;
}

#luckysheet {
  border-radius: 4px;
  /* 确保Luckysheet容器正确显示 */
  position: relative;
  overflow: hidden;
}

/* 确保Luckysheet工具栏显示正常 */
.luckysheet-wa-editor {
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif !important;
}

/* 优化Luckysheet在较大容器中的显示 */
.luckysheet-cell-main {
  height: 100% !important;
}

.luckysheet-grid-container {
  height: calc(100% - 100px) !important;
}
</style> 