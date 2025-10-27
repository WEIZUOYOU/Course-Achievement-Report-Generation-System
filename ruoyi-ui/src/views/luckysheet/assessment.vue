<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span style="font-size: 18px; font-weight: bold;">课程目标达成评价报告生成工具</span>
      </div>

      <!-- 步骤指示器 -->
      <el-steps :active="currentStep" align-center style="margin-bottom: 30px;">
        <el-step title="考核方式选择" description="选择需要的考核方式" />
        <el-step title="成绩占比设置" description="设置各考核方式的占比和总分" />
        <el-step title="课程目标设置" description="设置课程目标数量和内容" />
        <el-step title="支撑关系设置" description="设置考核方式与课程目标的支撑关系" />
        <el-step title="占比分配" description="分配各课程目标的具体占比" />
        <el-step title="试卷命题" description="设置期末试卷命题表" />
        <el-step title="数据录入" description="填写学生成绩" />
        <el-step title="数据处理" description="生成分析结果" />
        <el-step title="报告下载" description="下载评价报告" />
      </el-steps>

      <!-- 步骤1: 考核方式选择 -->
      <div v-if="currentStep === 0" class="box-card">
        <el-row :gutter="20">
          <el-col :span="24">
            <h3>第一步：考核方式选择</h3>
            <p>请选择本课程使用的考核方式</p>
            
            <el-checkbox-group v-model="selectedAssessmentTypes" @change="validateStep1">
              <el-checkbox label="regular" border>平时成绩</el-checkbox>
              <el-checkbox label="lab" border>上机成绩</el-checkbox>
              <el-checkbox label="final" border>期末考核</el-checkbox>
            </el-checkbox-group>
            
            <div class="step-actions">
              <el-button
                type="primary"
                size="large"
                :disabled="!isStep1Valid"
                @click="nextStep"
              >
                下一步
              </el-button>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 步骤2: 成绩占比设置 -->
      <div v-if="currentStep === 1" class="box-card">
        <el-row :gutter="20">
          <el-col :span="24">
            <h3>第二步：成绩占比设置</h3>
            <p>请设置各考核方式的占比和总分</p>
            
            <el-form :model="proportions" label-width="120px">
              <div class="input-sections">
                <!-- 平时成绩 -->
                <div v-if="selectedAssessmentTypes.includes('regular')" class="input-section">
                  <h4>平时成绩</h4>
                  <el-form-item label="占比：">
                    <el-input-number 
                      v-model="proportions.regular" 
                      :min="0" 
                      :max="100" 
                      :step="1" 
                      controls-position="right"
                      placeholder="请输入占比"
                    />%
                  </el-form-item>
                  <el-form-item label="总分：">
                    <el-input-number 
                      v-model="scores.regular" 
                      :min="1" 
                      :step="1" 
                      controls-position="right"
                      placeholder="请输入总分"
                    />
                  </el-form-item>
                </div>
                
                <!-- 上机成绩 -->
                <div v-if="selectedAssessmentTypes.includes('lab')" class="input-section">
                  <h4>上机成绩</h4>
                  <el-form-item label="占比：">
                    <el-input-number 
                      v-model="proportions.lab" 
                      :min="0" 
                      :max="100" 
                      :step="1" 
                      controls-position="right"
                      placeholder="请输入占比"
                    />%
                  </el-form-item>
                  <el-form-item label="总分：">
                    <el-input-number 
                      v-model="scores.lab" 
                      :min="1" 
                      :step="1" 
                      controls-position="right"
                      placeholder="请输入总分"
                    />
                  </el-form-item>
                </div>
                
                <!-- 期末考核 -->
                <div v-if="selectedAssessmentTypes.includes('final')" class="input-section">
                  <h4>期末考核</h4>
                  <el-form-item label="占比：">
                    <el-input-number 
                      v-model="proportions.final" 
                      :min="0" 
                      :max="100" 
                      :step="1" 
                      controls-position="right"
                      placeholder="请输入占比"
                    />%
                  </el-form-item>
                  <el-form-item label="总分：">
                    <el-input-number 
                      v-model="scores.final" 
                      :min="1" 
                      :step="1" 
                      controls-position="right"
                      placeholder="请输入总分"
                    />
                  </el-form-item>
                </div>
              </div>
            </el-form>
            
            <div class="validation-result" style="margin-top: 20px;">
              <el-alert 
                :title="totalMessage"
                :type="totalMessage.includes('错误') ? 'error' : (totalMessage.includes('成功') ? 'success' : 'info')"
                show-icon
                :closable="false"
              />
            </div>
            
            <div class="step-actions" style="margin-top: 20px;">
              <el-button @click="prevStep">上一步</el-button>
              <el-button type="primary" @click="validateProportions">验证占比</el-button>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 步骤3: 课程目标设置 -->
      <div v-if="currentStep === 2" class="box-card">
        <el-row :gutter="20">
          <el-col :span="24">
            <h3>第三步：课程目标设置</h3>
            <p>请设置课程目标数量</p>
            
            <el-form :model="{ targetCount }" label-width="120px">
              <el-form-item label="课程目标数量：">
                <el-input-number 
                  v-model="targetCount" 
                  :min="1" 
                  :max="10" 
                  :step="1" 
                  controls-position="right"
                />
              </el-form-item>
            </el-form>
            
            <div class="step-actions">
              <el-button @click="prevStep">上一步</el-button>
              <el-button 
                type="primary" 
                @click="generateTargetTable"
                :disabled="targetCount < 1 || targetCount > 10"
              >
                下一步
              </el-button>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 步骤4: 支撑关系设置 -->
      <div v-if="currentStep === 3" class="box-card">
        <el-row :gutter="20">
          <el-col :span="24">
            <h3>第四步：支撑关系设置</h3>
            <p>请为每个考核方式选择支撑的课程目标（至少选择一个）</p>
            
            <!-- 添加验证提示 -->
            <el-alert
              v-if="!hasSupportRelation"
              title="请至少为一个考核方式设置支撑关系"
              type="warning"
              :closable="false"
              style="margin-bottom: 15px;"
            />
            
            <el-table :data="supportRelationTable" style="width: 100%; margin-bottom: 20px;">
              <el-table-column prop="target" label="课程目标" width="120">
                <template slot-scope="scope">
                  目标{{ scope.$index + 1 }}
                </template>
              </el-table-column>
              
              <el-table-column v-if="selectedAssessmentTypes.includes('regular')" label="平时成绩" width="120">
                <template slot-scope="scope">
                  <el-checkbox v-model="scope.row.regular" @change="checkSupportRelations"></el-checkbox>
                </template>
              </el-table-column>
              
              <el-table-column v-if="selectedAssessmentTypes.includes('lab')" label="上机成绩" width="120">
                <template slot-scope="scope">
                  <el-checkbox v-model="scope.row.lab" @change="checkSupportRelations"></el-checkbox>
                </template>
              </el-table-column>
              
              <el-table-column v-if="selectedAssessmentTypes.includes('final')" label="期末考核" width="120">
                <template slot-scope="scope">
                  <el-checkbox v-model="scope.row.final" @change="checkSupportRelations"></el-checkbox>
                </template>
              </el-table-column>
            </el-table>
            
            <div class="step-actions">
              <el-button @click="prevStep">上一步</el-button>
              <el-button 
                type="primary" 
                @click="goToStep5"
                :disabled="!hasSupportRelation"
              >
                下一步
              </el-button>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 步骤5: 占比分配 -->
      <div v-if="currentStep === 4" class="box-card">
        <el-row :gutter="20">
          <el-col :span="24">
            <h3>第五步：占比分配</h3>
            <p>请为每个课程目标分配在各考核方式中的占比（每个考核方式内各课程目标占比之和必须为100%）</p>
            
            <!-- 加权计算说明 -->
            <el-alert
              title="加权计算说明"
              description="表格中输入的占比会乘以考核方式占比的加权系数，确保最终总占比为100%"
              type="info"
              style="margin-bottom: 20px;"
              :closable="false"
            />
            
            <el-table :data="targetProportionsTable" style="width: 100%; margin-bottom: 20px;">
              <el-table-column prop="target" label="课程目标" width="120">
                <template slot-scope="scope">
                  目标{{ scope.$index + 1 }}
                </template>
              </el-table-column>
              <el-table-column v-if="selectedAssessmentTypes.includes('regular')" label="平时成绩占比" width="180">
                <template slot-scope="scope">
                  <div>
                    <el-input-number 
                      v-model="scope.row.regular" 
                      :min="0" 
                      :max="100" 
                      :step="1" 
                      :disabled="!scope.row.regularSupported"
                      controls-position="right"
                      @change="updateTargetProportions"
                    />%
                  </div>
                  <div style="font-size: 12px; color: #909399; margin-top: 5px;">
                    加权后: {{ scope.row.weightedRegular.toFixed(2) }}%
                  </div>
                </template>
              </el-table-column>
              <el-table-column v-if="selectedAssessmentTypes.includes('lab')" label="上机成绩占比" width="180">
                <template slot-scope="scope">
                  <div>
                    <el-input-number 
                      v-model="scope.row.lab" 
                      :min="0" 
                      :max="100" 
                      :step="1" 
                      :disabled="!scope.row.labSupported"
                      controls-position="right"
                      @change="updateTargetProportions"
                    />%
                  </div>
                  <div style="font-size: 12px; color: #909399; margin-top: 5px;">
                    加权后: {{ scope.row.weightedLab.toFixed(2) }}%
                  </div>
                </template>
              </el-table-column>
              <el-table-column v-if="selectedAssessmentTypes.includes('final')" label="期末考核占比" width="180">
                <template slot-scope="scope">
                  <div>
                    <el-input-number 
                      v-model="scope.row.final" 
                      :min="0" 
                      :max="100" 
                      :step="1" 
                      :disabled="!scope.row.finalSupported"
                      controls-position="right"
                      @change="updateTargetProportions"
                    />%
                  </div>
                  <div style="font-size: 12px; color: #909399; margin-top: 5px;">
                    加权后: {{ scope.row.weightedFinal.toFixed(2) }}%
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="总占比" width="120">
                <template slot-scope="scope">
                  <div style="font-weight: bold;">
                    {{ scope.row.weightedTotal.toFixed(2) }}%
                  </div>
                </template>
              </el-table-column>
            </el-table>
            
            <!-- 考核方式占比汇总信息 -->
            <div class="proportion-summary" style="margin-bottom: 20px; padding: 15px; background: #f5f7fa; border-radius: 4px;">
              <p><strong>考核方式占比设置（来自步骤2）：</strong></p>
              <p v-if="selectedAssessmentTypes.includes('regular')">
                平时成绩占比: {{ proportions.regular }}% (加权系数: {{ (proportions.regular / 100).toFixed(2) }})
              </p>
              <p v-if="selectedAssessmentTypes.includes('lab')">
                上机成绩占比: {{ proportions.lab }}% (加权系数: {{ (proportions.lab / 100).toFixed(2) }})
              </p>
              <p v-if="selectedAssessmentTypes.includes('final')">
                期末考核占比: {{ proportions.final }}% (加权系数: {{ (proportions.final / 100).toFixed(2) }})
              </p>
              <p style="margin-top: 10px; font-weight: bold; color: #67c23a;">
                当前总加权占比: {{ getTotalWeightedProportion().toFixed(2) }}%
              </p>
            </div>
            
            <div class="validation-result">
              <el-alert 
                :title="proportionError"
                :type="proportionError.includes('错误') ? 'error' : 'success'"
                show-icon
                :closable="false"
              />
            </div>
            
            <div class="step-actions">
              <el-button @click="prevStep">上一步</el-button>
              <el-button type="primary" @click="validateAndProceed">验证并继续</el-button>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 步骤6: 期末试卷命题 -->
      <div v-if="currentStep === 5" class="box-card">
        <el-row :gutter="20">
          <el-col :span="24">
            <h3>第六步：期末试卷命题</h3>
            <p v-if="selectedAssessmentTypes.includes('final')">请设置期末试卷的大题和小题</p>
            <p v-else>您未选择期末考核，跳过此步骤</p>
            
            <div v-if="selectedAssessmentTypes.includes('final')">
              <!-- 试卷设置界面 -->
              <div class="table-controls">
                <el-button type="primary" @click="addRow">添加大题</el-button>
                <el-button type="danger" @click="removeRow" :disabled="rows.length <= 1">删除大题</el-button>
                <el-button type="primary" @click="addColumn">添加小题</el-button>
                <el-button type="danger" @click="removeColumn" :disabled="columns <= 1">删除小题</el-button>
              </div>
              
              <!-- 修复表格渲染 -->
              <el-table :data="rows" style="width: 100%; margin-top: 20px;" border>
                <el-table-column label="大题" width="80" align="center">
                  <template slot-scope="scope">
                    大题{{ scope.$index + 1 }}
                  </template>
                </el-table-column>
                
                <!-- 动态生成小题列 -->
                <el-table-column 
                  v-for="(col, colIndex) in columns" 
                  :key="colIndex"
                  :label="`小题${colIndex + 1}`" 
                  width="180"
                  align="center"
                >
                  <template slot-scope="scope">
                    <div style="margin-bottom: 10px;">
                      <el-input-number
                        v-model="scope.row.cells[colIndex].score"
                        :min="0"
                        :max="100"
                        :step="1"
                        size="small"
                        placeholder="分值"
                        @change="updateTotals"
                      />
                    </div>
                    <div>
                      <el-select
                        v-model="scope.row.cells[colIndex].target"
                        placeholder="选择目标"
                        size="small"
                        style="width: 100%"
                        @change="updateTotals"
                      >
                        <el-option
                          v-for="target in targetOptions"
                          :key="target"
                          :label="target"
                          :value="target"
                        />
                      </el-select>
                    </div>
                  </template>
                </el-table-column>
                
                <el-table-column label="大题总分" width="100" align="center">
                  <template slot-scope="scope">
                    <span style="font-weight: bold; color: #409EFF;">{{ scope.row.total }}</span>
                  </template>
                </el-table-column>
              </el-table>
              
              <div class="total-score">
                <h4>试卷总分: {{ totalScore }} / {{ scores.final }} 分</h4>
              </div>
              
              <div class="validation-result">
                <el-alert 
                  :title="paperValidationMessage"
                  :type="paperValidationType"
                  show-icon
                  :closable="false"
                />
              </div>
            </div>
            
            <div class="step-actions">
              <el-button @click="prevStep">上一步</el-button>
              <el-button 
                type="primary" 
                @click="handleStep6Next"
                :loading="step6.loading"
                :disabled="step6.loading"
              >
                {{ step6.loading ? '正在生成数据...' : '下一步' }}
              </el-button>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 步骤7: 数据录入 -->
      <div v-if="currentStep === 6" class="box-card">
        <el-row :gutter="20">
          <el-col :span="24">
            <h3>第七步：数据录入</h3>
            <!-- 更新描述文本 -->
            <p>请在各sheet中填写学生成绩数据（请从第一行开始填写实际数据）</p>
            
            <!-- 显示加载状态 -->
            <div v-if="step7.isLoading" class="loading-overlay">
              <el-alert title="正在加载表格模板..." type="info" :closable="false" />
            </div>
            
            <!-- 纯iframe方式 -->
            <div class="luckysheet-fullscreen">
              <iframe
                ref="luckysheetFrame"
                :src="luckysheetUrl"
                frameborder="0"
                width="100%"
                height="100%"
                @load="onLuckysheetLoad"
              ></iframe>
            </div>
            
            <!-- 添加使用提示 -->
            <el-alert
              title="使用说明"
              description="请直接在表格中填写学生数据，第一行为表头，从第二行开始填写实际学生信息"
              type="info"
              :closable="false"
              style="margin-bottom: 15px;"
            />
            
            <div class="step-actions">
              <el-button @click="prevStep">上一步</el-button>
              <el-button 
                type="primary" 
                @click="processData"
                :disabled="!step7.hasData"
                :loading="processing"
              >
                {{ processing ? '处理中...' : '开始处理数据' }}
              </el-button>
              <el-button 
                type="warning" 
                @click="clearSheetData"
                :disabled="step7.processing"
              >
                清除数据
              </el-button>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 步骤8：数据处理 -->
      <div v-if="currentStep === 7" class="box-card">
        <el-row :gutter="20">
          <el-col :span="24">
            <h3>第八步：数据处理中</h3>
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

      <!-- 步骤9：结果展示和报告下载 -->
      <div v-if="currentStep === 8" class="box-card">
        <el-row :gutter="20">
          <el-col :span="24">
            <h3>第九步：结果展示与报告下载</h3>

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
      // 步骤控制
      currentStep: 0,
      loading: false,

      // 考核方式选择
      selectedAssessmentTypes: ['regular', 'lab', 'final'],
      isStep1Valid: false,
      
      // 考核配置
      proportions: {
        regular: 0,
        lab: 0,
        final: 0
      },
      scores: {
        regular: 0,
        lab: 0,
        final: 0 
      },
      totalMessage: '',
      
      // 课程目标
      targetCount: 2,
      supportRelationTable: [],
      
      // 占比分配
      targetProportionsTable: [],
      proportionError: '',
      
      // 期末试卷
      rows: [],
      columns: 1,
      totalScore: 0,
      paperValidationMessage: '请设置试卷',
      paperValidationType: 'info',
      isStep6Valid: false,
      
      // 处理结果
      configId: '',
      reportId: '',
      results: {},
      activeTab: 'gradeDistribution',
      
      // 最终配置
      finalConfig: null,

      // LuckySheet 相关
      luckysheetUrl: process.env.VUE_APP_BASE_API + "/luckysheet.html",

      // 配置文件
      examConfig: null,
    
      // 步骤6专用状态 - 优化结构
      step6: {
        loading: false,
        lastValidation: null
      },
      
      // 步骤7专用状态 - 优化结构
      step7: {
        isInitialized: true,
        isLoading: true,
        hasData: false,
        luckysheetReady: false,
        processing: false,
        progress: 0,
        processMessage: '准备处理数据...',
        fileGenerationStatus: '',
        validationResult: {
          isValid: false,
          message: '等待数据验证...',
          studentCount: 0,
          sheetCount: 0,
          issues: []
        },
        lastError: null
      },

      // LuckySheet 数据
      luckysheetData: null, // 添加 LuckySheet 数据存储

      processing: false,
      processProgress: 0,
      processStatus: null,
      processMessage: '准备处理数据...',
      activeTab: 'gradeDistribution',
      configId: '',
      reportId: '',
      results: {}
    }
  },
  
  computed: {
    targetOptions() {
      return Array.from({ length: this.targetCount }, (_, i) => `目标${i + 1}`)
    },

      // 验证是否有支撑关系
    hasSupportRelation() {
      return this.supportRelationTable.some(row => 
        row.regular || row.lab || row.final
      )
    }
  },
  
  mounted() {
    document.title = '课程目标达成评价报告 - 若依管理系统'
    this.validateStep1()
  },
  
  methods: {
    // ========== 步骤导航方法 ==========
    nextStep() {
      if (!this.validateCurrentStep()) {
        return
      }

      // 特殊处理：步骤7不直接进入步骤8，而是调用processData
      if (this.currentStep === 6) {
        this.processData() // 调用数据处理函数
        return // 不执行常规的步骤跳转
      }
      
      // 执行步骤特定的初始化
      this.initializeNextStep()
      
      this.currentStep++
      
      // 如果进入步骤7，确保初始化
      if (this.currentStep === 6) {
        this.$nextTick(() => {
          setTimeout(() => {
            this.initializeStep7()
          }, 500)
        })
      }
    },
    
    prevStep() {
      if (this.currentStep > 0) {
        this.currentStep--
      }
    },
    
    // 统一的步骤验证函数
    validateCurrentStep() {
      switch (this.currentStep) {
        case 0: // 步骤1
          return this.selectedAssessmentTypes.length > 0
          
        case 1: // 步骤2
          return this.totalMessage.includes('验证成功')
          
        case 2: // 步骤3
          return this.targetCount >= 1 && this.targetCount <= 10
          
        case 3: // 步骤4
          return this.hasSupportRelation
          
        case 4: // 步骤5
          return this.proportionError.includes('验证成功')
        
        case 5: // 步骤6
          if (this.selectedAssessmentTypes.includes('final')) {
            const validationResult = this.validateExamPaper()
            // 允许有警告但继续，只有错误才阻止
            return validationResult.errors.length === 0
          }
          return true
          
        case 6: // 步骤7（数据录入）
          return this.step7.hasData || (this.step7.validationResult && this.step7.validationResult.studentCount > 0)
        
        case 7: // 步骤8（数据处理）- 新增验证
          return this.processing === false // 只有在非处理状态才能进入
        
        case 8: // 步骤9（结果展示）- 新增验证
          return this.results && Object.keys(this.results).length > 0
              
        default:
          return true
      }
    },
    
    // 初始化下一步骤
    initializeNextStep() {
      switch (this.currentStep) {
        case 0: // 步骤1 -> 步骤2
          // 不需要特殊初始化
          break
          
        case 1: // 步骤2 -> 步骤3
          // 不需要特殊初始化
          break
          
        case 2: // 步骤3 -> 步骤4
          this.generateTargetTable()
          break
          
        case 3: // 步骤4 -> 步骤5
          console.log('从步骤4进入步骤5，初始化目标占比表')
          this.initTargetProportionsTable()
          break
          
        case 4: // 步骤5 -> 步骤6
          if (this.selectedAssessmentTypes.includes('final')) {
            this.initializeExamTable()
          }
          break
          
        case 5: // 步骤6 -> 步骤7
          this.step7.isInitialized = false
          break
          
        case 6: // 步骤7 -> 步骤8
          // 不需要特殊初始化，processData会处理
          break
          
        case 7: // 步骤8 -> 步骤9
          // 不需要特殊初始化，processData会自动跳转
          break
      }
    },
    
    // ========== 步骤1: 考核方式选择 ==========
    validateStep1() {
      this.isStep1Valid = this.selectedAssessmentTypes.length > 0
      console.log('选中的考核方式:', this.selectedAssessmentTypes)
    },
    
    // ========== 步骤2: 成绩占比设置 ==========
    validateProportions() {
      const total = this.proportions.regular + this.proportions.lab + this.proportions.final
      if (total === 100) {
        this.totalMessage = '验证成功：占比设置正确'
        this.nextStep()
      } else {
        this.totalMessage = `错误：总占比必须为100%，当前为${total}%`
      }
    },
    
    // ========== 步骤3: 课程目标设置 ==========
    generateTargetTable() {
      // 先验证目标数量
      if (this.targetCount < 1 || this.targetCount > 10) {
        this.$message.error('请设置有效的课程目标数量（1-10）')
        return
      }
      
      // 生成支撑关系表
      this.supportRelationTable = Array.from({ length: this.targetCount }, (_, index) => ({
        target: `目标${index + 1}`,
        regular: true,
        lab: true,
        final: true
      }))
      
      // 直接进入下一步，不调用 nextStep() 避免重复验证
      this.currentStep++
    },

    // ========== 步骤4: 支撑关系设置 ==========
    goToStep5() {
      console.log('点击进入步骤5，当前支撑关系表:', this.supportRelationTable)
      
      // 重新验证支撑关系
      const hasSupportRelation = this.supportRelationTable && 
        this.supportRelationTable.some(row => 
          (row.regular !== undefined && row.regular) || 
          (row.lab !== undefined && row.lab) || 
          (row.final !== undefined && row.final)
        )
      
      console.log('支撑关系验证结果:', hasSupportRelation)
      
      if (!hasSupportRelation) {
        this.$message.error('请至少为一个考核方式设置课程目标支撑关系')
        return
      }
      
      // 使用统一的下一步方法
      this.nextStep()
    },

    checkSupportRelations() {
      console.log('支撑关系发生变化:', this.supportRelationTable)
      // 这里可以添加额外的验证逻辑
    },

    // ========== 步骤5: 占比分配 ==========
    initTargetProportionsTable() {
      console.log('初始化目标占比表，目标数量:', this.targetCount)
      console.log('支撑关系表:', this.supportRelationTable)
      
      this.targetProportionsTable = this.supportRelationTable.map((row, index) => {
        // 计算该目标支持的考核方式数量
        const supportedTypes = []
        if (row.regular) supportedTypes.push('regular')
        if (row.lab) supportedTypes.push('lab')
        if (row.final) supportedTypes.push('final')
        
        // 平均分配占比到支持的考核方式
        const defaultProportion = supportedTypes.length > 0 ? Math.round(100 / supportedTypes.length) : 0
        
        const targetProportion = {
          target: `目标${index + 1}`,
          regular: row.regular ? defaultProportion : 0,
          lab: row.lab ? defaultProportion : 0,
          final: row.final ? defaultProportion : 0,
          regularSupported: row.regular,
          labSupported: row.lab,
          finalSupported: row.final,
          weightedRegular: 0,
          weightedLab: 0,
          weightedFinal: 0,
          weightedTotal: 0
        }
        
        console.log(`目标${index + 1}配置:`, targetProportion)
        return targetProportion
      })
      
      // 立即更新加权占比
      this.updateTargetProportions()
      
      console.log('最终目标占比表:', this.targetProportionsTable)
    },
    
    updateTargetProportions() {
      this.targetProportionsTable.forEach(row => {
        // 计算加权后的占比
        // 加权占比 = 输入占比 × 考核方式占比 / 100
        row.weightedRegular = (row.regular * this.proportions.regular) / 100
        row.weightedLab = (row.lab * this.proportions.lab) / 100
        row.weightedFinal = (row.final * this.proportions.final) / 100
        
        // 计算总加权占比
        row.weightedTotal = row.weightedRegular + row.weightedLab + row.weightedFinal
      })
    },

    // 获取总加权占比
    getTotalWeightedProportion() {
      return this.targetProportionsTable.reduce((sum, row) => sum + row.weightedTotal, 0)
    },
    
    calculateTargetProportions() {
      this.updateTargetProportions()
      
      let isValid = true
      const errorMessages = []
      
      // 验证每个考核方式内部各课程目标的输入占比之和是否为100%
      
      // 验证平时成绩
      if (this.selectedAssessmentTypes.includes('regular')) {
        const regularTotal = this.targetProportionsTable
          .filter(row => row.regularSupported)
          .reduce((sum, row) => sum + (row.regular || 0), 0)
        
        if (Math.abs(regularTotal - 100) > 0.01) {
          isValid = false
          errorMessages.push(`平时成绩中各课程目标输入占比之和为${regularTotal.toFixed(2)}%（应为100%）`)
        }
      }
      
      // 验证上机成绩
      if (this.selectedAssessmentTypes.includes('lab')) {
        const labTotal = this.targetProportionsTable
          .filter(row => row.labSupported)
          .reduce((sum, row) => sum + (row.lab || 0), 0)
        
        if (Math.abs(labTotal - 100) > 0.01) {
          isValid = false
          errorMessages.push(`上机成绩中各课程目标输入占比之和为${labTotal.toFixed(2)}%（应为100%）`)
        }
      }
      
      // 验证期末考核
      if (this.selectedAssessmentTypes.includes('final')) {
        const finalTotal = this.targetProportionsTable
          .filter(row => row.finalSupported)
          .reduce((sum, row) => sum + (row.final || 0), 0)
        
        if (Math.abs(finalTotal - 100) > 0.01) {
          isValid = false
          errorMessages.push(`期末考核中各课程目标输入占比之和为${finalTotal.toFixed(2)}%（应为100%）`)
        }
      }
      
      // 验证总加权占比是否为100%
      const totalWeighted = this.getTotalWeightedProportion()
      if (Math.abs(totalWeighted - 100) > 0.01) {
        isValid = false
        errorMessages.push(`总加权占比为${totalWeighted.toFixed(2)}%（应为100%）`)
      }
      
      // 更新验证消息
      if (isValid) {
        this.proportionError = '验证成功：各考核方式内课程目标占比分配正确，总加权占比为100%'
      } else {
        this.proportionError = `错误：${errorMessages.join('；')}`
      }

      return isValid // 返回验证结果
    },

    // 步骤5的验证并继续
    validateAndProceed() {
      try {
        console.log('=== 开始步骤5验证 ===')
        
        // 执行验证
        const isValid = this.calculateTargetProportions()
        
        console.log('验证结果:', isValid)
        console.log('错误信息:', this.proportionError)
        console.log('当前步骤:', this.currentStep)
        
        if (isValid && this.proportionError.includes('验证成功')) {
          console.log('验证通过，准备跳转')
          
          // 如果是步骤5，需要初始化试卷表格（如果选择了期末考核）
          if (this.currentStep === 4 && this.selectedAssessmentTypes.includes('final')) {
            this.initializeExamTable()
          }
          
          // 使用统一的下一步方法
          this.nextStep()
          
          console.log('跳转后步骤:', this.currentStep)
        } else {
          console.log('验证未通过')
          this.$message.warning('请先完成占比分配验证')
        }
        
      } catch (error) {
        console.error('验证过程出错:', error)
        this.$message.error('验证过程发生错误: ' + error.message)
      }
    },
    
    // ========== 步骤6: 期末试卷命题相关方法 ==========
    addRow() {
      const newRow = {
        cells: Array(this.columns).fill().map(() => ({ 
          score: 0, 
          target: '' 
        })),
        total: 0
      }
      this.rows.push(newRow)
      this.updateTotals()
    },

    removeRow() {
      if (this.rows.length > 1) {
        this.rows.pop()
        this.updateTotals()
      } else {
        this.$message.warning('至少需要保留一个大题')
      }
    },

    addColumn() {
      this.columns++
      this.rows.forEach(row => {
        row.cells.push({ score: 0, target: '' })
      })
      this.updateTotals()
    },

    removeColumn() {
      if (this.columns > 1) {
        this.columns--
        this.rows.forEach(row => {
          row.cells.pop()
        })
        this.updateTotals()
      } else {
        this.$message.warning('至少需要保留一个小题')
      }
    },

    updateTotals() {
      // 计算每行总分
      this.rows.forEach(row => {
        row.cells.forEach(cell => {
          // 如果分值为0，自动清除目标选择
          if (cell.score <= 0 && cell.target) {
            cell.target = ''
          }
        })
        row.total = row.cells.reduce((sum, cell) => {
          const score = Number(cell.score) || 0
          return sum + score
        }, 0)
      })
      
      // 计算试卷总分
      this.totalScore = this.rows.reduce((sum, row) => sum + row.total, 0)
      
      // 验证试卷
      this.validateExamPaper()
    },

    // 试卷表格初始化
    initializeExamTable() {
      console.log('初始化试卷表格')
      
      // 如果还没有行数据，初始化默认的试卷结构
      if (this.rows.length === 0) {
        this.rows = [
          {
            cells: Array(this.columns).fill().map(() => ({ 
              score: 0, 
              target: '' 
            })),
            total: 0
          }
        ]
      }
      
      // 更新总分
      this.updateTotals()
      
      console.log('试卷表格初始化完成，行数:', this.rows.length)
    },

    // 统一的试卷验证函数
    validateExamPaper() {
      const errors = []
      const warnings = []
      
      // 1. 总分验证
      if (this.totalScore !== this.scores.final) {
        errors.push(`试卷总分${this.totalScore}与期末考核总分${this.scores.final}不匹配`)
      }
      
      // 2. 目标分配验证
      let hasUnassignedTarget = false
      let totalQuestions = 0
      const targetScoreMap = new Map()
      
      this.rows.forEach((row, rowIndex) => {
        row.cells.forEach((cell, colIndex) => {
          if (cell.score > 0) {
            totalQuestions++
            
            if (!cell.target) {
              hasUnassignedTarget = true
              errors.push(`大题${rowIndex + 1}小题${colIndex + 1}有分值但未分配课程目标`)
            } else {
              // 统计各目标分值
              const currentScore = targetScoreMap.get(cell.target) || 0
              targetScoreMap.set(cell.target, currentScore + cell.score)
            }
          }
        })
      })
      
      // 3. 题目数量验证
      if (totalQuestions === 0) {
        errors.push('试卷中没有任何有分值的题目')
      }
      
      // 4. 目标覆盖验证
      const supportedTargets = this.supportRelationTable
        .filter(row => row.final)
        .map(row => row.target)
      
      const usedTargets = Array.from(targetScoreMap.keys())
      const uncoveredTargets = supportedTargets.filter(target => 
        !usedTargets.includes(target)
      )
      
      if (uncoveredTargets.length > 0) {
        warnings.push(`以下课程目标在试卷中未覆盖: ${uncoveredTargets.join(', ')}`)
      }
      
      // 更新验证状态
      if (errors.length === 0) {
        this.paperValidationMessage = warnings.length > 0 
          ? `试卷设置基本正确！${warnings.join('；')}`
          : '试卷设置正确！总分匹配且所有题目分配合理'
        this.paperValidationType = warnings.length > 0 ? 'warning' : 'success'
        this.isStep6Valid = true
      } else {
        this.paperValidationMessage = `发现${errors.length}个问题：${errors.join('；')}${warnings.length > 0 ? `；${warnings.join('；')}` : ''}`
        this.paperValidationType = 'error'
        this.isStep6Valid = false
      }
      
      return {
        isValid: errors.length === 0,
        errors,
        warnings,
        totalQuestions,
        targetCoverage: {
          supported: supportedTargets,
          used: usedTargets,
          uncovered: uncoveredTargets
        }
      }
    },

    // 修改步骤6的下一步处理
    async handleStep6Next() {
      if (this.step6.loading) return
      
      try {
        this.step6.loading = true
        
        // 1. 生成课程配置
        this.ensureCourseConfig()
        
        // 2. 验证试卷结构
        const validationResult = this.validateExamPaper()
        
        // 3. 特别检查期末考核的设置
        if (this.selectedAssessmentTypes.includes('final')) {
          const finalValidation = this.validateFinalExamStructure()
          if (!finalValidation.isValid) {
            this.$message.warning(finalValidation.message)
          }
        }
        
        // 4. 跳转到步骤7
        this.currentStep = 6
        
        // 5. 初始化步骤7的表格
        this.$nextTick(() => {
          setTimeout(() => {
            this.initializeStep7()
          }, 100)
        })
        
      } catch (error) {
        console.error('步骤6处理失败:', error)
        this.$message.error('步骤6处理失败: ' + error.message)
      } finally {
        this.step6.loading = false
      }
    },

    // 验证期末试卷结构
    validateFinalExamStructure() {
      if (!this.selectedAssessmentTypes.includes('final')) {
        return { isValid: true, message: '未选择期末考核' }
      }
      
      const issues = []
      
      // 检查是否有大题
      if (this.rows.length === 0) {
        issues.push('请至少添加一个大题')
      }
      
      // 检查是否有小题
      let totalQuestions = 0
      this.rows.forEach((row, index) => {
        const questionsInRow = row.cells.filter(cell => cell.score > 0).length
        totalQuestions += questionsInRow
        
        if (questionsInRow === 0) {
          issues.push(`大题${index + 1}没有设置小题`)
        }
      })
      
      if (totalQuestions === 0) {
        issues.push('试卷中没有设置任何小题')
      }
      
      // 检查总分匹配
      if (this.totalScore !== this.scores.final) {
        issues.push(`试卷总分${this.totalScore}与设置的期末总分${this.scores.final}不匹配`)
      }
      
      return {
        isValid: issues.length === 0,
        message: issues.length > 0 ? issues.join('；') : '试卷结构正确',
        issues,
        totalQuestions
      }
    },

    // 确保课程配置生成
    ensureCourseConfig() {
      if (!this.examConfig) {
        this.examConfig = this.generateCourseConfig()
      }
      return this.examConfig
    },

    // 统一的配置生成函数
    generateCourseConfig() {
      // 计算各考核方式占比
      const regularGrade = this.proportions.regular || 0
      const labGrade = this.proportions.lab || 0
      const finalExam = this.proportions.final || 0

      // 计算课程目标占比
      const courseTargetProportions = this.targetProportionsTable.map(row => ({
        courseTarget: row.target,
        regularGrade: row.weightedRegular,
        lab: row.weightedLab,
        finalExam: row.weightedFinal,
        total: row.weightedTotal
      }))

      // 生成试卷结构 - 修复格式
      const examPaper = []
      if (this.selectedAssessmentTypes.includes('final')) {
        this.rows.forEach((row, sectionIndex) => {
          const questions = []
          
          // 过滤掉分值为0的小题，并按列顺序排序
          row.cells.forEach((cell, questionIndex) => {
            if (cell.score > 0 && cell.target) {
              questions.push({
                questionNumber: questionIndex + 1, // 小题号从1开始
                score: cell.score,
                target: cell.target
              })
            }
          })
          
          // 按小题号排序
          const sortedQuestions = questions.sort((a, b) => a.questionNumber - b.questionNumber)
          
          if (sortedQuestions.length > 0) {
            examPaper.push({
              title: (sectionIndex + 1).toString(), // 大题号作为字符串，如 "1", "2"
              questions: sortedQuestions
            })
          }
        })
      }

      const config = {
        // ... 其他配置 ...
        examPaper: examPaper,
        assessmentTypes: [...this.selectedAssessmentTypes]
      }
      
      console.log('生成的标准化课程配置 - examPaper:', config.examPaper)
      return config
    },

    // 显示试卷问题警告
    async showExamPaperWarning(validationResult) {
      const errorCount = validationResult.errors.length
      const warningCount = validationResult.warnings.length
      
      let message = ''
      if (errorCount > 0) {
        message = `发现 ${errorCount} 个错误问题，${warningCount} 个警告：\n\n`
        message += validationResult.errors.slice(0, 3).map(err => `• ${err}`).join('\n')
        if (errorCount > 3) {
          message += `\n... 还有 ${errorCount - 3} 个错误`
        }
        if (warningCount > 0) {
          message += `\n\n警告：\n`
          message += validationResult.warnings.slice(0, 2).map(warn => `• ${warn}`).join('\n')
        }
      } else if (warningCount > 0) {
        message = `发现 ${warningCount} 个警告问题：\n\n`
        message += validationResult.warnings.slice(0, 5).map(warn => `• ${warn}`).join('\n')
        if (warningCount > 5) {
          message += `\n... 还有 ${warningCount - 5} 个警告`
        }
      }
      
      message += '\n\n是否继续生成数据模板？'
      
      try {
        await this.$confirm(
          message,
          '试卷设置警告',
          {
            confirmButtonText: '继续生成',
            cancelButtonText: '返回修改',
            type: errorCount > 0 ? 'error' : 'warning',
            dangerouslyUseHTMLString: false
          }
        )
        return true
      } catch {
        return false
      }
    },

    // 创建普通考核sheet
    createAssessmentSheet(type, name, config) {
      const baseHeaders = ['班级', '学号', '姓名', '成绩']
      
      return {
        name: name,
        color: type === 'regular' ? '#409EFF' : type === 'lab' ? '#67C23A' : '#E6A23C',
        index: this.luckysheetData.length,
        status: 1,
        order: this.luckysheetData.length,
        celldata: [
          // 表头行
          { r: 0, c: 0, v: '班级' },
          { r: 0, c: 1, v: '学号' },
          { r: 0, c: 2, v: '姓名' },
          { r: 0, c: 3, v: '成绩' }
        ],
        config: {
          columnlen: { 0: 100, 1: 120, 2: 80, 3: 90 }
        }
      }
    },

    // 创建期末考核sheet（特殊处理）
    createFinalExamSheet(type, name, config) {
      const headers = ['班级', '学号', '姓名']
      
      // 添加试卷题目列
      if (config.examPaper && config.examPaper.sections) {
        config.examPaper.sections.forEach(section => {
          section.questions.forEach(question => {
            headers.push(`大题${section.section}.${question.number}`)
          })
        })
      }
      
      return {
        name: name,
        celldata: this.generateCellData(headers),
        config: {
          columnlen: this.generateColumnConfig(headers),
          rowlen: 40
        }
      }
    },

    // 生成单元格数据
    generateCellData(headers) {
      const celldata = []
      
      // 生成表头行
      headers.forEach((header, colIndex) => {
        celldata.push({
          r: 0,
          c: colIndex,
          v: header
        })
      })
      
      return celldata
    },

    // 生成列配置
    generateColumnConfig(headers) {
      const columnConfig = {}
      headers.forEach((header, index) => {
        if (index === 0) columnConfig[index] = 100   // 班级列
        else if (index === 1) columnConfig[index] = 120 // 学号列
        else if (index === 2) columnConfig[index] = 80  // 姓名列
        else columnConfig[index] = 90 // 成绩列
      })
      return columnConfig
    },

    // 添加调试方法
    checkLuckySheetStatus() {
      console.log('LuckySheet 状态检查:')
      console.log('- window.luckysheet:', !!window.luckysheet)
      console.log('- 容器元素:', document.getElementById('luckysheet'))
      console.log('- 步骤7状态:', this.step7)
      console.log('- 数据:', this.luckysheetData)
    },

    // ========== 步骤7: 数据处理相关方法 ==========
    
    // 统一的步骤7初始化
    initializeStep7() {
      console.log('开始初始化步骤7')
      this.step7.isLoading = true
      
      // 生成模板数据（现在只是用于配置验证）
      this.generateLuckysheetTemplateData()
      
      // 重置iframe以触发重新加载
      this.luckysheetUrl = process.env.VUE_APP_BASE_API + "/luckysheet.html?t=" + Date.now()
    },

    // iframe加载完成回调
    async onLuckysheetLoad() {
      console.log('LuckySheet iframe加载完成')
      
      try {
        // 等待Luckysheet完全初始化
        await this.waitForLuckysheetReady()
        
        // 通过JavaScript向iframe传递数据
        await this.loadDataToLuckysheet()
        
        this.step7.isLoading = false
        this.step7.luckysheetReady = true
        console.log('模板数据加载完成')
        
      } catch (error) {
        console.error('加载数据失败:', error)
        this.step7.isLoading = false
        this.$message.error('加载表格模板失败，请刷新页面重试')
      }
    },

    // 等待Luckysheet就绪
    waitForLuckysheetReady() {
      return new Promise((resolve, reject) => {
        let attempts = 0
        const maxAttempts = 50 // 最多尝试5秒
        
        const checkLuckysheet = () => {
          attempts++
          
          try {
            const iframe = this.$refs.luckysheetFrame
            if (!iframe || !iframe.contentWindow) {
              throw new Error('iframe未就绪')
            }
            
            const luckysheet = iframe.contentWindow.luckysheet
            if (luckysheet && typeof luckysheet.create === 'function') {
              console.log('Luckysheet已就绪')
              resolve()
            } else {
              throw new Error('Luckysheet对象未就绪')
            }
          } catch (error) {
            if (attempts >= maxAttempts) {
              reject(new Error('Luckysheet加载超时'))
            } else {
              setTimeout(checkLuckysheet, 100)
            }
          }
        }
        
        checkLuckysheet()
      })
    },

    // 向Luckysheet加载数据
    async loadDataToLuckysheet() {
      return new Promise((resolve, reject) => {
        try {
          const iframe = this.$refs.luckysheetFrame
          const luckysheet = iframe.contentWindow.luckysheet
          
          // 生成Luckysheet格式的数据
          const luckysheetData = this.generateLuckysheetData()
          console.log('生成的Luckysheet数据:', luckysheetData)
          
          // 销毁现有实例（如果有）
          if (typeof luckysheet.destroy === 'function') {
            luckysheet.destroy()
          }
          
          // 延迟创建以确保DOM就绪
          setTimeout(() => {
            try {
              // 创建新的Luckysheet实例
              luckysheet.create({
                container: 'luckysheet', // LuckySheet会自动查找这个id的容器
                lang: 'zh',
                data: luckysheetData,
                title: '学生成绩数据表',
                userInfo: false,
                showtoolbar: true,
                showinfobar: false,
                showsheetbar: true,
                showstatisticBar: true,
                cellRightClickConfig: {
                  copy: true,
                  copyAs: true,
                  paste: true,
                  insertRow: true,
                  insertColumn: true,
                  deleteRow: true,
                  deleteColumn: true,
                  deleteCell: true,
                  hideRow: true,
                  hideColumn: true,
                  rowHeight: true,
                  columnWidth: true,
                  clear: true
                }
              })
              
              console.log('Luckysheet创建成功，加载了', luckysheetData.length, '个sheet')
              resolve()
              
            } catch (error) {
              reject(new Error('创建Luckysheet失败: ' + error.message))
            }
          }, 500)
          
        } catch (error) {
          reject(new Error('加载数据到Luckysheet失败: ' + error.message))
        }
      })
    },

    // 生成Luckysheet格式的数据
    generateLuckysheetData() {
      const config = this.ensureCourseConfig()
      const sheets = []
      
      // 为每个选中的考核方式创建sheet
      if (this.selectedAssessmentTypes.includes('regular')) {
        sheets.push(this.createRegularSheet())
      }
      
      if (this.selectedAssessmentTypes.includes('lab')) {
        sheets.push(this.createLabSheet())
      }
      
      if (this.selectedAssessmentTypes.includes('final')) {
        sheets.push(this.createFinalSheet(config))
      }
      
      return sheets
    },

    // 创建平时成绩sheet - 严格按照CSV格式
    createRegularSheet() {
      const headers = ['班级', '学号', '姓名', '平时成绩总分']
      const celldata = []
      
      // 只生成表头和一空行数据
      const data = [
        headers,
        ['', '', '', ''] // 空行供填写
      ]
      
      // 转换为Luckysheet格式
      data.forEach((row, rowIndex) => {
        row.forEach((cell, colIndex) => {
          celldata.push({
            r: rowIndex,
            c: colIndex,
            v: cell
          })
        })
      })
      
      return {
        name: '平时成绩',
        color: '#409EFF',
        index: 0,
        status: 1,
        order: 0,
        celldata: celldata,
        config: {
          columnlen: {
            0: 100,
            1: 120,
            2: 80,
            3: 90
          }
        }
      }
    },

    // 创建上机成绩sheet - 严格按照CSV格式
    createLabSheet() {
      const headers = ['班级', '学号', '姓名', '上机成绩总分']
      const celldata = []
      
      const data = [
        headers,
        ['', '', '', ''] // 空行供填写
      ]
      
      data.forEach((row, rowIndex) => {
        row.forEach((cell, colIndex) => {
          celldata.push({
            r: rowIndex,
            c: colIndex,
            v: cell
          })
        })
      })
      
      return {
        name: '上机成绩',
        color: '#67C23A',
        index: 1,
        status: 1,
        order: 1,
        celldata: celldata,
        config: {
          columnlen: {
            0: 100,
            1: 120,
            2: 80,
            3: 90
          }
        }
      }
    },

    // 创建期末成绩sheet
    createFinalSheet(config) {
      const headers = ['班级', '学号', '姓名']
      const celldata = []
      
      console.log('生成期末成绩表，完整配置:', config)
      console.log('试卷结构 examPaper:', config.examPaper)
      
      // 调试：检查数据结构
      if (config.examPaper && Array.isArray(config.examPaper)) {
        console.log('examPaper 是数组，长度:', config.examPaper.length)
        config.examPaper.forEach((section, index) => {
          console.log(`第${index}个大题:`, section)
          console.log(`- title: ${section.title}`)
          console.log(`- questions:`, section.questions)
        })
      } else {
        console.warn('examPaper 不存在或不是数组:', config.examPaper)
      }
      
      // 生成小题列头 - 增强兼容性
      if (config.examPaper && Array.isArray(config.examPaper) && config.examPaper.length > 0) {
        config.examPaper.forEach((section) => {
          // 确保 section 和 questions 存在
          if (section && section.questions && Array.isArray(section.questions)) {
            // 按小题号排序
            const sortedQuestions = [...section.questions].sort((a, b) => 
              (a.questionNumber || 0) - (b.questionNumber || 0)
            )
            
            sortedQuestions.forEach((question) => {
              if (question && question.score > 0) {
                // 使用安全的方式获取大题号和小题号
                const sectionTitle = section.title || '1'
                const questionNumber = question.questionNumber || 1
                const score = question.score || 0
                
                // 生成标准格式: "大题号.小题号（分值分）"
                const header = `${sectionTitle}.${questionNumber}（${score}分）`
                headers.push(header)
                console.log(`添加列头: ${header}`)
              }
            })
          }
        })
      } else {
        // 备用方案：从 rows 数据生成
        console.log('使用备用方案从 rows 生成列头')
        this.generateHeadersFromRows(headers)
      }
      
      console.log('最终表头:', headers)
      
      // 生成表头行
      headers.forEach((header, colIndex) => {
        celldata.push({
          r: 0, // 第1行
          c: colIndex,
          v: header
        })
      })
      
      // 生成一行空数据行（供填写成绩）
      headers.forEach((_, colIndex) => {
        celldata.push({
          r: 1, // 第2行
          c: colIndex,
          v: '' // 空值，等待用户填写
        })
      })
      
      // 生成列宽配置
      const columnlen = {}
      headers.forEach((header, index) => {
        if (index === 0) columnlen[index] = 100   // 班级列
        else if (index === 1) columnlen[index] = 120 // 学号列
        else if (index === 2) columnlen[index] = 80  // 姓名列
        else columnlen[index] = 120 // 小题列（更宽以显示完整格式）
      })
      
      return {
        name: '期末成绩',
        color: '#E6A23C',
        index: 2,
        status: 1,
        order: 2,
        celldata: celldata,
        config: {
          columnlen: columnlen,
          rowlen: 25 // 行高
        }
      }
    },
  
    // 新增备用列头生成方法
    generateHeadersFromRows(headers) {
      if (this.rows && this.rows.length > 0) {
        this.rows.forEach((row, rowIndex) => {
          if (row.cells && Array.isArray(row.cells)) {
            row.cells.forEach((cell, cellIndex) => {
              if (cell && cell.score > 0) {
                const header = `${rowIndex + 1}.${cellIndex + 1}（${cell.score}分）`
                headers.push(header)
                console.log(`从rows生成列头: ${header}`)
              }
            })
          }
        })
      }
      
      // 如果还是没有列头，添加默认列
      if (headers.length <= 3) {
        for (let i = 1; i <= 10; i++) {
          headers.push(`题目${i}`)
        }
      }
    },

    // 同时需要修改试卷验证方法，确保小题号正确
    updateTotals() {
      // 计算每行总分
      this.rows.forEach(row => {
        row.cells.forEach((cell, index) => {
          // 如果分值为0，自动清除目标选择
          if (cell.score <= 0 && cell.target) {
            cell.target = ''
          }
          // 确保小题号正确（从1开始）
          cell.questionNumber = index + 1
        })
        row.total = row.cells.reduce((sum, cell) => {
          const score = Number(cell.score) || 0
          return sum + score
        }, 0)
      })
      
      // 计算试卷总分
      this.totalScore = this.rows.reduce((sum, row) => sum + row.total, 0)
      
      // 验证试卷
      this.validateExamPaper()
    },

    // 生成LuckySheet模板数据（清理后）
    generateLuckysheetTemplateData() {
      try {
        const config = this.ensureCourseConfig()
        
        // 构建完整的表格配置
        const templateConfig = {
          proportions: config.proportions,
          scores: config.scores,
          targets: config.targets,
          assessmentTypes: config.assessmentTypes
          // 不再需要 sheets 字段，因为数据会通过 generateLuckysheetData() 生成
        }
        
        this.examConfig = templateConfig
        console.log('生成的模板配置:', this.examConfig)
        
        return templateConfig
        
      } catch (error) {
        console.error('生成模板数据失败:', error)
        throw new Error(`模板数据生成失败: ${error.message}`)
      }
    },

    // 生成表头单元格数据
    generateHeaderCells(headers) {
      const celldata = [];
      
      headers.forEach((header, colIndex) => {
        celldata.push({
          r: 0, // 行索引，0表示第一行
          c: colIndex, // 列索引
          v: {
            v: header, // 单元格值
            m: header, // 显示值
            ct: { fa: "General", t: "g" }, // 单元格格式
            bg: '#f0f9ff', // 背景色
            fs: 12, // 字体大小
            ff: 'Microsoft YaHei', // 字体
            bl: 1, // 粗体
            it: 0, // 斜体
            vt: 0 // 垂直对齐
          }
        });
      });
      
      return celldata;
    },

    // 生成列宽配置
    generateColumnWidths(headers) {
      const columnlen = {};
      headers.forEach((_, index) => {
        if (index === 0) columnlen[index] = 100;   // 班级列
        else if (index === 1) columnlen[index] = 120; // 学号列
        else if (index === 2) columnlen[index] = 80;  // 姓名列
        else columnlen[index] = 90; // 成绩列
      });
      return columnlen;
    },

    // 获取sheet名称列表
    getSheetNames() {
      return this.luckysheetData.map(sheet => sheet.name).join('、');
    },

    // 初始化LuckySheet并加载数据
    async initializeLuckySheetWithData() {
      return new Promise((resolve, reject) => {
        // 等待LuckySheet加载完成
        const checkLuckySheet = () => {
          const iframe = this.$refs.luckysheetFrame;
          if (!iframe || !iframe.contentWindow) {
            setTimeout(checkLuckySheet, 100);
            return;
          }
          
          try {
            const luckysheet = iframe.contentWindow.luckysheet;
            if (!luckysheet) {
              setTimeout(checkLuckySheet, 100);
              return;
            }
            
            // 销毁现有实例
            if (typeof luckysheet.destroy === 'function') {
              luckysheet.destroy();
            }
            
            // 创建新实例并加载数据
            setTimeout(() => {
              try {
                luckysheet.create({
                  container: 'luckysheet',
                  lang: 'zh',
                  plugins: ['chart'],
                  data: this.luckysheetData,
                  title: '学生成绩数据表',
                  userInfo: false,
                  userMenuItem: [],
                  showtoolbar: true,
                  showinfobar: false,
                  showsheetbar: true,
                  showstatisticBar: true
                });
                
                console.log('LuckySheet 初始化成功，加载了', this.luckysheetData.length, '个sheet');
                resolve();
              } catch (error) {
                reject(error);
              }
            }, 500);
            
          } catch (error) {
            reject(error);
          }
        };
        
        checkLuckySheet();
        
        // 设置超时
        setTimeout(() => {
          reject(new Error('LuckySheet 初始化超时'));
        }, 10000);
      });
    },

    // 带重试机制的LuckySheet初始化
    async initializeLuckysheetWithRetry(maxRetries = 3) {
      for (let attempt = 1; attempt <= maxRetries; attempt++) {
        try {
          console.log(`LuckySheet初始化尝试 ${attempt}/${maxRetries}`)
          
          const luckysheet = this.getLuckysheetInstance()
          if (!luckysheet) {
            throw new Error('LuckySheet实例未找到')
          }
          
          // 检查是否已有数据，避免重复初始化
          const hasExistingData = await this.checkExistingData()
          if (hasExistingData) {
            console.log('检测到已有数据，保留现有数据')
            this.step7.hasData = true
            return true
          }
          
          // 加载模板数据
          await this.loadTemplateData(luckysheet)
          this.step7.hasData = false // 新加载的模板没有学生数据
          
          console.log('LuckySheet初始化成功')
          return true
          
        } catch (error) {
          console.warn(`LuckySheet初始化尝试 ${attempt} 失败:`, error)
          
          if (attempt === maxRetries) {
            throw new Error(`LuckySheet初始化失败，已重试${maxRetries}次: ${error.message}`)
          }
          
          // 等待一段时间后重试
          await new Promise(resolve => setTimeout(resolve, 1000 * attempt))
        }
      }
    },

    // 获取LuckySheet实例
    getLuckysheetInstance() {
      try {
        const iframe = this.$refs.luckysheetFrame
        if (!iframe || !iframe.contentWindow) {
          throw new Error('LuckySheet iframe 未找到或未加载完成')
        }
        
        const luckysheet = iframe.contentWindow.luckysheet
        if (!luckysheet) {
          throw new Error('LuckySheet 全局对象不存在')
        }
        
        return luckysheet
      } catch (error) {
        console.error('获取 LuckySheet 实例时出错:', error)
        return null
      }
    },

    // 检查是否已有数据
    async checkExistingData() {
      const luckysheet = this.getLuckysheetInstance()
      if (!luckysheet) return false
      
      const allSheets = luckysheet.getAllSheets()
      if (!allSheets || allSheets.length === 0) return false
      
      // 检查是否有实际的学生数据（不仅仅是表头）
      for (const sheet of allSheets) {
        const data = luckysheet.getSheetData(sheet.id)
        if (data && data.length > 1) {
          // 检查第2行（索引1）是否有数据
          const firstDataRow = data[1]
          if (firstDataRow && firstDataRow.length >= 3) {
            const classInfo = firstDataRow[0] && firstDataRow[0].v
            const studentId = firstDataRow[1] && firstDataRow[1].v
            const studentName = firstDataRow[2] && firstDataRow[2].v
            
            if (classInfo || studentId || studentName) {
              return true
            }
          }
        }
      }
      
      return false
    },

    // 加载模板数据
    async loadTemplateData(luckysheet) {
      return new Promise((resolve, reject) => {
        try {
          const container = 'luckysheet'
          
          // 销毁现有实例
          if (typeof luckysheet.destroy === 'function') {
            luckysheet.destroy()
          }
          
          // 重新创建表格
          setTimeout(() => {
            try {
              luckysheet.create({
                container: container,
                data: this.luckysheetData,
                title: '学生成绩数据表',
                lang: 'zh'
              })
              resolve()
            } catch (error) {
              reject(error)
            }
          }, 500)
          
        } catch (error) {
          reject(error)
        }
      })
    },

    // 提取行数据
    extractRowData(row) {
      if (!row) return []
      
      const result = []
      const maxColumns = Math.max(...this.luckysheetData.map(sheet => 
        sheet.celldata.filter(cell => cell.r === 0).length
      )) || 10
      
      for (let i = 0; i < maxColumns; i++) {
        const cell = row[i]
        if (cell && typeof cell === 'object' && cell.v !== undefined) {
          result.push(cell.v)
        } else {
          result.push(cell || '')
        }
      }
      return result
    },

    // 检查是否有有效的学生数据
    hasValidStudentData(rowData) {
      if (!rowData || rowData.length < 3) return false
      
      const [studentClass, studentId, studentName, ...scores] = rowData
      
      // 必须有学号或姓名
      if (!studentId && !studentName) return false
      
      // 必须有至少一个成绩数据（且不是空字符串）
      return scores.some(score => {
        // 检查是否为有效数字且大于0
        const scoreNum = Number(score)
        return !isNaN(scoreNum) && scoreNum > 0
      })
    },

    // 验证学生数据
    async validateStudentData(extractedData) {
      const issues = []
      let totalStudents = 0
      const studentMap = new Map()
      
      // 检查是否有任何数据
      if (Object.keys(extractedData).length === 0) {
        return {
          isValid: false,
          message: '没有找到任何数据',
          issues: ['请在各表格中填写学生成绩数据'],
          studentCount: 0,
          sheetCount: 0
        }
      }
      
      for (const [sheetName, sheetData] of Object.entries(extractedData)) {
        const { headers, rows } = sheetData
        
        if (!headers || headers.length < 3) {
          issues.push(`${sheetName}: 表头不完整，至少需要班级、学号、姓名`)
          continue
        }
        
        if (rows.length === 0) {
          issues.push(`${sheetName}: 没有学生数据`)
          continue
        }
        
        // 验证数据行
        let sheetStudentCount = 0
        rows.forEach((row, index) => {
          const rowNumber = index + 2
          
          const studentClass = row[0] || ''
          const studentId = row[1] || ''
          const studentName = row[2] || ''
          
          // 基本验证：学号和姓名至少有一个
          if (!studentId && !studentName) {
            issues.push(`${sheetName}第${rowNumber}行: 缺少学号和姓名`)
            return
          }
          
          // 检查学号重复
          if (studentId) {
            if (studentMap.has(studentId)) {
              issues.push(`${sheetName}第${rowNumber}行: 学号 ${studentId} 重复`)
            } else {
              studentMap.set(studentId, {
                sheet: sheetName,
                row: rowNumber,
                name: studentName
              })
            }
          }
          
          // 检查成绩数据有效性（从第4列开始）
          let validScores = 0
          let invalidScores = []
          
          for (let i = 3; i < row.length; i++) {
            const score = row[i]
            if (score !== '' && score != null) {
              const scoreNum = Number(score)
              if (isNaN(scoreNum)) {
                invalidScores.push(`第${i + 1}列`)
              } else if (scoreNum >= 0) {
                validScores++
              } else {
                invalidScores.push(`第${i + 1}列(负数)`)
              }
            }
          }
          
          if (invalidScores.length > 0) {
            issues.push(`${sheetName}第${rowNumber}行: 无效成绩数据 [${invalidScores.join(', ')}]`)
          }
          
          if (validScores > 0) {
            sheetStudentCount++
            totalStudents++
          } else {
            issues.push(`${sheetName}第${rowNumber}行: 没有有效的成绩数据`)
          }
        })
        
        console.log(`${sheetName} 有效学生数:`, sheetStudentCount)
      }
      
      const isValid = totalStudents > 0
      const message = isValid 
        ? `数据验证通过，共发现 ${totalStudents} 名学生的有效数据`
        : `没有找到有效的学生成绩数据，请检查数据填写`
      
      return {
        isValid,
        message,
        issues,
        studentCount: totalStudents,
        sheetCount: Object.keys(extractedData).length,
        duplicateStudents: Array.from(studentMap.entries()).filter(([id, info]) => 
          issues.some(issue => issue.includes(id))
        )
      }
    },

    // 显示验证结果并确认
    async showValidationResult(validation) {
      if (validation.studentCount === 0) {
        await this.$alert(
          '没有找到有效的学生数据，请先填写成绩信息。\n\n确保：\n• 填写了学号或姓名\n• 至少有一个有效的成绩数据\n• 成绩数据为非负数',
          '数据验证失败',
          {
            confirmButtonText: '返回修改',
            type: 'error'
          }
        )
        return false
      }
      
      if (validation.issues.length > 0) {
        const issueList = validation.issues.slice(0, 8).map(issue => `• ${issue}`).join('\n')
        const moreIssues = validation.issues.length > 8 ? `\n... 还有 ${validation.issues.length - 8} 个问题` : ''
        
        try {
          await this.$confirm(
            `共发现 ${validation.studentCount} 名有效学生，但存在 ${validation.issues.length} 个问题：\n\n${issueList}${moreIssues}\n\n是否继续处理？`,
            '数据验证警告',
            {
              confirmButtonText: '继续处理',
              cancelButtonText: '返回修改',
              type: 'warning',
              customClass: 'validation-dialog',
              closeOnClickModal: false
            }
          )
          return true
        } catch {
          return false
        }
      }
      
      return true
    },

    // 清除数据（保留表头）
    async clearSheetData() {
      try {
        await this.$confirm(
          '确定要清除所有学生数据吗？这将重新加载初始模板。',
          '确认清除数据',
          {
            confirmButtonText: '确定清除',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        this.step7.isLoading = true
        // 重新初始化步骤7
        this.initializeStep7()
        
        this.$message.success('数据清除完成，已重新加载模板')
        
      } catch (error) {
        if (error !== 'cancel') {
          console.error('清除数据失败:', error)
          this.$message.error('清除数据失败: ' + (error.message || error))
        }
      }
    },

    // 统一的错误处理
    handleStepError(stepName, error) {
      console.error(`${stepName} 出错:`, error)
      
      const errorConfig = {
        'Network Error': '网络连接失败，请检查网络状态',
        'Timeout': '请求超时，请稍后重试',
        'Luckysheet not ready': '表格组件加载失败，请刷新页面',
        '未找到任何成绩表格': '没有找到数据表格，请确保已正确初始化'
      }
      
      const userMessage = errorConfig[error.message] || 
        (error.message.includes('失败') ? error.message : `${stepName}失败: ${error.message}`)
      
      this.$message.error(userMessage)
      
      // 记录错误信息用于调试
      this.step7.lastError = {
        step: stepName,
        message: error.message,
        timestamp: new Date().toISOString(),
        userMessage: userMessage
      }
    },

    // 修改步骤7的下一步按钮处理
    async handleStep7Next() {
      try {
        this.step7.processing = true
        this.step7.fileGenerationStatus = '正在验证和处理数据...'
        
        // 1. 提取和验证数据
        const extractedData = await this.extractSheetData()
        const validation = await this.validateStudentData(extractedData)
        this.step7.validationResult = validation
        
        // 2. 根据验证结果处理
        if (!validation.isValid || validation.studentCount === 0) {
          const shouldContinue = await this.showValidationResult(validation)
          if (!shouldContinue) {
            return
          }
        }
        
        // 3. 设置数据状态，但不立即处理
        this.step7.hasData = true
        this.step7.fileGenerationStatus = `验证通过，共 ${validation.studentCount} 名学生数据`
        
        this.$message.success(`数据验证通过，共 ${validation.studentCount} 名学生数据`)
        
      } catch (error) {
        this.handleStepError('步骤7数据处理', error)
        this.step7.fileGenerationStatus = '数据处理失败，请检查数据格式'
      } finally {
        this.step7.processing = false
      }
    },

    // ========== 步骤8: 结果展示和报告下载 ==========
    getImageUrl(filename) {
      // 在实际应用中，这里应该返回真实的图片URL
      // 暂时使用模拟URL
      return `/api/images/${filename}?t=${Date.now()}`
    },
    
    handleImageError(event, filename) {
      console.error(`图片加载失败: ${filename}`, event)
      // 可以显示一个占位图或错误信息
      event.target.style.display = 'none'
      
      // 修复这一行：将可选链操作符改为传统判断
      if (event.target.nextElementSibling) {
        event.target.nextElementSibling.style.display = 'block'
      }
    },
    
    downloadReport() {
      try {
        // 模拟报告下载
        this.$message.info('开始生成并下载报告...')
        
        // 在实际应用中，这里应该调用后端API下载Word报告
        // 暂时使用模拟下载
        setTimeout(() => {
          this.$message.success('报告下载完成！')
          
          // 创建模拟下载
          const blob = new Blob(['模拟报告内容'], { type: 'application/msword' })
          const url = URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = `课程目标达成评价报告_${new Date().toLocaleDateString()}.doc`
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          URL.revokeObjectURL(url)
        }, 1500)
        
      } catch (error) {
        console.error('下载报告失败:', error)
        this.$message.error('下载报告失败：' + error.message)
      }
    },

    // 数据处理方法
    async processData() {
      this.processing = true
      this.currentStep = 7  // 跳转到数据处理步骤
      this.processProgress = 0
      this.processStatus = null
      this.processMessage = '正在提取Excel数据...'

      let progressTimer = null

      try {
        // 获取Luckysheet数据
        const excelData = await this.extractSheetData()
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

        // 生成配置ID
        this.configId = this.generateConfigId()
        
        // 提交数据处理
        const response = await processData({
          excelData: excelData,
          config: this.examConfig,
          configId: this.configId
        })
        
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
            this.currentStep = 8  // 跳转到结果展示步骤
          }, 1000)
        } else {
          throw new Error('数据处理API返回错误: ' + (response.msg || '未知错误'))
        }
      } catch (error) {
        if (progressTimer) {
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

    // 生成配置ID
    generateConfigId() {
      const timestamp = new Date().getTime()
      const random = Math.random().toString(36).substring(2, 8)
      return `config_${timestamp}_${random}.json`
    },

    // 图片URL生成方法
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

    // 图片加载错误处理
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

    // 下载报告方法
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


    
    // ========== 工具方法 ==========
    restart() {
      // 重置所有数据到初始状态
      this.currentStep = 0
      this.selectedAssessmentTypes = ['regular', 'lab', 'final']
      this.isStep1Valid = true
      
      // 重置为null而不是默认值
      this.proportions = {
        regular: 0,
        lab: 0,
        final: 0
      }
      this.scores = {
        regular: 0,
        lab: 0,
        final: 0
      }
      this.totalMessage = '请设置各考核方式的占比和总分'
      
      this.targetCount = 2
      this.supportRelationTable = []
      this.targetProportionsTable = []
      this.proportionError = ''
      
      this.rows = []
      this.columns = 1
      this.totalScore = 0
      this.paperValidationMessage = '请设置试卷'
      this.paperValidationType = 'info'
      this.isStep6Valid = false
      
      this.configId = ''
      this.reportId = ''
      this.results = {}
      this.activeTab = 'gradeDistribution'
      this.finalConfig = null
      
      this.examConfig = null
      this.csvHeaders = {
        final: [],
        regular: [],
        lab: []
      }
      
      // 重置步骤7
      this.step7 = {
        isInitialized: false,
        isLoading: false,
        hasData: false,
        luckysheetReady: false,
        luckysheetInstance: null,
        sheetData: null,
        validationResult: {
          isValid: false,
          message: '等待数据验证...',
          dataCount: 0,
          issues: []
        },
        processing: false,
        progress: 0,
        processMessage: '准备处理数据...'
      }
      
      this.step7NeedsInitialization = false
      
      // 重新初始化
      this.validateStep1()
      
      this.$message.success('已重置所有配置，可以重新开始')
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

/* 新增样式 */
.step {
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  margin-bottom: 20px;
}

.input-group {
  margin-bottom: 15px;
}

.input-group label {
  margin-right: 20px;
  margin-bottom: 10px;
  display: inline-block;
}

.input-group input {
  margin-left: 5px;
  width: 60px;
  padding: 5px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

button {
  background-color: #409eff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
}

button:hover {
  background-color: #66b1ff;
}

.error {
  color: #f56c6c;
}

.success {
  color: #67c23a;
}

/* LuckySheet 容器样式 */
.luckysheet-container {
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 20px;
  background: #f8f9fa;
}

.luckysheet-container iframe {
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.button-group {
  margin-top: 15px;
}

.button-group .el-button {
  margin: 0 5px;
}

/* 步骤操作按钮 */
.step-actions {
  margin-top: 20px;
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
}

/* 进度显示 */
.process-status {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
  margin: 20px 0;
}

/* 全屏LuckySheet容器 */
.luckysheet-fullscreen {
  position: relative;
  width: 100%;
  height: 600px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 20px;
  background: #f8f9fa;
}

.luckysheet-fullscreen iframe {
  width: 100%;
  height: 100%;
  border: none;
}

/* 加载状态 */
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

</style>