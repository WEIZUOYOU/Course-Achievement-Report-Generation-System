<template>
  <div id="app">
    <h2 class="progress-info">步骤 1/6: 选择需要的考核方式</h2>
    <form id="assessmentSelectionForm">
      <label><input type="checkbox" v-model="regularCheck" checked /> 平时成绩</label><br />
      <label><input type="checkbox" v-model="labCheck" checked /> 上机成绩</label><br />
      <label><input type="checkbox" v-model="finalCheck" checked /> 期末考核</label><br />
      <button type="button" @click="proceedToGradeForm">下一步</button>
    </form>

    <div id="gradeFormSection" v-show="showGradeForm">
      <h2 class="progress-info">步骤 2/6: 输入平时成绩、上机成绩、期末考核占比和总分</h2>
      <form id="gradeForm">
        <div id="regularGradeDiv" v-show="regularCheck">
          <label for="regularGrade">平时成绩占比 (%): </label>
          <input
            type="number"
            v-model="regularGrade"
            name="regularGrade"
            min="0"
            max="100"
          /><br /><br />
          <label for="regularTotalScore">平时成绩总分: </label>
          <input
            type="number"
            v-model="regularTotalScore"
            name="regularTotalScore"
            min="1"
          /><br /><br />
        </div>

        <div id="labGradeDiv" v-show="labCheck">
          <label for="labGrade">上机成绩占比 (%): </label>
          <input
            type="number"
            v-model="labGrade"
            name="labGrade"
            min="0"
            max="100"
          /><br /><br />
          <label for="labTotalScore">上机成绩总分: </label>
          <input
            type="number"
            v-model="labTotalScore"
            name="labTotalScore"
            min="1"
          /><br /><br />
        </div>

        <div id="finalExamDiv" v-show="finalCheck">
          <label for="finalExam">期末考核占比 (%): </label>
          <input
            type="number"
            v-model="finalExam"
            name="finalExam"
            min="0"
            max="100"
          /><br /><br />
          <label for="finalTotalScore">期末考核总分: </label>
          <input
            type="number"
            v-model="finalTotalScore"
            name="finalTotalScore"
            min="1"
          /><br /><br />
        </div>

        <button type="button" @click="validateTotal">检测总占比</button>
      </form>

      <p id="totalPercentage">{{ totalPercentageMsg }}</p>
    </div>

    <div id="courseTargetSection" v-show="showCourseTarget">
      <h2 class="progress-info">步骤 3/6: 输入课程目标</h2>
      <label for="courseTargetCount">请输入课程目标数量: </label>
      <input
        type="number"
        v-model="courseTargetCount"
        name="courseTargetCount"
        min="1"
        required
      /><br /><br />
      <button type="button" @click="generateAssessmentTable">
        生成考核方式表格
      </button>
    </div>

    <div id="assessmentTableSection" v-show="showAssessmentTable">
      <h2 class="progress-info">步骤 4/6: 选择课程目标支撑</h2>
      <h2>考核方式与课程目标支撑</h2>
      <table border="1" id="assessmentTable">
        <thead>
          <tr>
            <th>考核方式</th>
            <th>支撑课程目标</th>
          </tr>
        </thead>
        <tbody>
          <tr id="regularAssessmentRow" v-show="regularCheck">
            <td>平时成绩</td>
            <td id="regularSupportTargets">
              <div v-for="i in courseTargetCount" :key="i">
                <input
                  type="checkbox"
                  :value="'课程目标' + i"
                  v-model="regularTargets[i - 1]"
                />
                <label>课程目标{{ i }}</label>
              </div>
            </td>
          </tr>
          <tr id="finalAssessmentRow" v-show="finalCheck">
            <td>期末考试</td>
            <td id="finalSupportTargets">
              <div v-for="i in courseTargetCount" :key="i">
                <input
                  type="checkbox"
                  :value="'课程目标' + i"
                  v-model="finalTargets[i - 1]"
                />
                <label>课程目标{{ i }}</label>
              </div>
            </td>
          </tr>
          <tr id="labAssessmentRow" v-show="labCheck">
            <td>实验考核</td>
            <td id="labSupportTargets">
              <div v-for="i in courseTargetCount" :key="i">
                <input
                  type="checkbox"
                  :value="'课程目标' + i"
                  v-model="labTargets[i - 1]"
                />
                <label>课程目标{{ i }}</label>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <button type="button" @click="calculateTargetProportions">
        计算课程目标占比
      </button>
    </div>

    <div id="targetProportionSection" v-show="showTargetProportion">
      <h2 class="progress-info">步骤 5/6: 课程目标中各考核方式占比</h2>
      <h2>课程目标中各考核方式占比</h2>
      <table border="1" id="proportionTable">
        <thead>
          <tr>
            <th>课程目标</th>
            <th v-show="regularCheck">平时成绩 (%)</th>
            <th v-show="finalCheck">期末考试 (%)</th>
            <th v-show="labCheck">实验考核 (%)</th>
            <th>合计 (%)</th>
          </tr>
        </thead>
        <tbody id="proportionTableBody">
          <tr v-for="(target, index) in courseTargetProportions" :key="index">
            <td>课程目标{{ index + 1 }}</td>
            <td v-show="regularCheck">{{ target.regularGrade.toFixed(2) }}</td>
            <td v-show="finalCheck">{{ target.finalExam.toFixed(2) }}</td>
            <td v-show="labCheck">{{ target.lab.toFixed(2) }}</td>
            <td>{{ target.total.toFixed(2) }}</td>
          </tr>
        </tbody>
        <tfoot>
          <tr>
            <th>成绩比例 % 合计</th>
            <td v-show="regularCheck">{{ regularTotal.toFixed(2) }}</td>
            <td v-show="finalCheck">{{ finalTotal.toFixed(2) }}</td>
            <td v-show="labCheck">{{ labTotal.toFixed(2) }}</td>
            <td>{{ overallTotal.toFixed(2) }}</td>
          </tr>
        </tfoot>
      </table>
      <p id="proportionError" style="color: red">{{ proportionError }}</p>
      <button type="button" @click="generateExamPaperTable">
        生成期末试卷命题表
      </button>
    </div>

    <div id="examPaperSection" v-show="showExamPaper">
      <h2 class="progress-info">步骤 6/6: 填写期末试卷命题表</h2>
      <h2>期末试卷命题表</h2>
      <button type="button" @click="addRow">添加大题</button>
      <button type="button" @click="addColumn">添加小题</button>
      <button type="button" @click="deleteRow">删除大题</button>
      <button type="button" @click="deleteColumn">删除小题</button>
      <table border="1" id="examPaperTable">
        <thead>
          <tr id="examPaperHeader">
            <th>大题\\小题</th>
            <th v-for="col in columns" :key="col">{{ col }}</th>
          </tr>
        </thead>
        <tbody id="examPaperBody">
          <tr v-for="(row, rowIndex) in rows" :key="rowIndex">
            <th>大题{{ rowIndex + 1 }}</th>
            <td
              v-for="(colIndex, cellIndex) in columns.length"
              :key="cellIndex"
            >
              <input
                type="number"
                placeholder="小题分值"
                min="0"
                max="100"
                v-model="row.cells[cellIndex].score"
              />
              <select v-model="row.cells[cellIndex].target">
                <option value="">请选择课程目标</option>
                <option v-for="i in courseTargetCount" :key="i">
                  课程目标{{ i }}
                </option>
              </select>
            </td>
            <td class="row-total">{{ rowTotal(rowIndex) }}</td>
          </tr>
        </tbody>
      </table>
      <button type="button" @click="generateHeaderInfo">
        生成配置文件和成绩模板
      </button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      regularCheck: true,
      labCheck: true,
      finalCheck: true,
      regularGrade: 0,
      labGrade: 0,
      finalExam: 0,
      regularTotalScore: 0,
      labTotalScore: 0,
      finalTotalScore: 0,
      totalPercentageMsg: "",
      courseTargetCount: 0,
      regularTargets: [],
      finalTargets: [],
      labTargets: [],
      courseTargetProportions: [],
      regularTotal: 0,
      finalTotal: 0,
      labTotal: 0,
      overallTotal: 0,
      proportionError: "",
      showGradeForm: false,
      showCourseTarget: false,
      showAssessmentTable: false,
      showTargetProportion: false,
      showExamPaper: false,
      rows: [],
      columns: ["小题1"],
      onlineTableUrl: "https://在线表格页面地址.com", // 替换为实际的在线表格页面地址
    };
  },
  methods: {
    proceedToGradeForm() {
      this.showGradeForm = true;
    },
    validateTotal() {
      const total =
        parseFloat(this.regularGrade) +
        parseFloat(this.labGrade) +
        parseFloat(this.finalExam);
      if (total !== 100) {
        this.totalPercentageMsg = `错误: 总占比必须为 100%，当前为: ${total}%`;
      } else {
        this.totalPercentageMsg = `总占比正确: ${total}%`;
        this.showCourseTarget = true;
      }
    },
    generateAssessmentTable() {
      this.regularTargets = new Array(this.courseTargetCount).fill(false);
      this.finalTargets = new Array(this.courseTargetCount).fill(false);
      this.labTargets = new Array(this.courseTargetCount).fill(false);
      this.showAssessmentTable = true;
    },
    calculateTargetProportions() {
      this.courseTargetProportions = [];
      this.regularTotal = 0;
      this.finalTotal = 0;
      this.labTotal = 0;

      for (let i = 0; i < this.courseTargetCount; i++) {
        let regularProportion = this.regularTargets[i]
          ? parseFloat(
              prompt(`请输入平时成绩中课程目标${i + 1}所占的百分比：`)
            ) || 0
          : 0;
        let finalProportion = this.finalTargets[i]
          ? parseFloat(
              prompt(`请输入期末考试中课程目标${i + 1}所占的百分比：`)
            ) || 0
          : 0;
        let labProportion = this.labTargets[i]
          ? parseFloat(
              prompt(`请输入实验考核中课程目标${i + 1}所占的百分比：`)
            ) || 0
          : 0;

        if (this.regularCheck) this.regularTotal += regularProportion;
        if (this.finalCheck) this.finalTotal += finalProportion;
        if (this.labCheck) this.labTotal += labProportion;

        this.courseTargetProportions.push({
          courseTarget: `课程目标${i + 1}`,
          regularGrade: regularProportion,
          finalExam: finalProportion,
          lab: labProportion,
          total: regularProportion + finalProportion + labProportion,
        });
      }

      this.overallTotal = this.regularTotal + this.finalTotal + this.labTotal;
      this.showTargetProportion = true;
    },
    generateExamPaperTable() {
      this.rows = [{ cells: [{ score: 0, target: "" }] }];
      this.showExamPaper = true;
    },
    addRow() {
      this.rows.push({ cells: new Array(this.columns.length).fill({ score: 0, target: "" }) });
    },
    addColumn() {
      this.columns.push(`小题${this.columns.length + 1}`);
      this.rows.forEach((row) => {
        row.cells.push({ score: 0, target: "" });
      });
    },
    deleteRow() {
      if (this.rows.length > 1) {
        this.rows.pop();
      }
    },
    deleteColumn() {
      if (this.columns.length > 1) {
        this.columns.pop();
        this.rows.forEach((row) => {
          row.cells.pop();
        });
      }
    },
    rowTotal(rowIndex) {
      let total = 0;
      this.rows[rowIndex].cells.forEach((cell) => {
        total += parseFloat(cell.score) || 0;
      });
      return total.toFixed(2);
    },
    generateHeaderInfo() {
      let finalExamHeader = "班级\t学号\t姓名";
      this.rows.forEach((row, rowIndex) => {
        row.cells.forEach((cell, colIndex) => {
          if (parseFloat(cell.score) > 0) {
            let questionNumber = `${rowIndex + 1}.${colIndex + 1}`;
            finalExamHeader += `\t${questionNumber}（${cell.score}分）`;
          }
        });
      });

      let headerInfo = {
        finalExamHeader: finalExamHeader,
        labHeader: "班级\t学号\t姓名\t上机成绩总分",
        regularHeader: "班级\t学号\t姓名\t平时成绩总分",
      };

      // 将表头信息转换为JSON字符串，然后进行Base64编码
      let encodedHeaderInfo = btoa(JSON.stringify(headerInfo));

      // 构建在线表格页面的URL，包含表头信息作为查询参数
      let url = `${this.onlineTableUrl}?headerInfo=${encodedHeaderInfo}`;

      // 打开在线表格页面
      window.open(url, "_blank");
    },
  },
};
</script>

<style>
input[type="number"] {
  width: 60px; /* Reduce the width of input elements to accommodate smaller values */
}
.zero-score {
  background-color: #f0f0f0; /* Light grey to indicate the question is not included */
  color: #888; /* Grey text to further indicate non-existent question */
}
.progress-info {
  font-weight: bold;
  color: #003399;
  margin-bottom: 10px;
}
</style>