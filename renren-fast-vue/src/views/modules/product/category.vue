<!--  -->
<template>
    <div>
        <el-button type="danger" plain size="small" @click="batchDelete">批量删除</el-button>
        <el-tree :data="menus" :props="defaultProps" :expand-on-click-node="false" show-checkbox
            :default-expanded-keys="expandedKey" node-key="catId" ref="menuTree">
            <span class="custom-tree-node" slot-scope="{ node, data }">
                <span>{{ node.label }}</span>
                <span>
                    <el-button v-if="node.level <= 2" type="text" size="mini" @click="() => append(data)">
                        添加
                    </el-button>
                    <el-button type="text" size="mini" @click="() => edit(data)">
                        修改
                    </el-button>
                    <el-button v-if="node.childNodes.length == 0" type="text" size="mini"
                        @click="() => remove(node, data)">
                        删除
                    </el-button>
                </span>
            </span>
        </el-tree>

        <el-dialog :title="title" :visible.sync="dialogFormVisible" width="30%" :close-on-click-modal="false">
            <el-form :model="category">
                <el-form-item label="分类名称">
                    <el-input v-model="category.name" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="图标">
                    <el-input v-model="category.icon" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="计量单位">
                    <el-input v-model="category.productUnit" autocomplete="off"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitData">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
//这里可以导入其他文件（比如：组件，工具js，第三方插件js，json文件，图片文件等等）
//例如：import 《组件名称》 from '《组件路径》';

export default {
    //import引入的组件需要注入到对象中才能使用
    components: {},
    data() {
        return {
            title: "",
            dialogType: "", // add、edit
            category: {
                name: "",
                parentCid: 0,
                catLevel: 0,
                sort: 0,
                showStatus: 1,
                catId: null,
                icon: "",
                productUnit: "",
            },
            dialogFormVisible: false,
            menus: [],
            expandedKey: [],
            defaultProps: {
                children: 'children',
                label: 'name'
            },
        };
    },
    //监听属性 类似于data概念
    computed: {},
    //监控data中的数据变化
    watch: {},
    //方法集合
    methods: {
        // 获取数据列表
        getMenus() {
            this.$http({
                url: this.$http.adornUrl("/product/category/list/tree"),
                method: "get"
            }).then(({ data }) => {
                console.log("成功获取到菜单数据...", data.data);
                this.menus = data.data;
            });
        },

        // 批量删除
        batchDelete() {
            let catIds = [];
            let catNames = [];
            // this.$refs表示当前el-tree的所有引用
            let checkNodes = this.$refs.menuTree.getCheckedNodes();
            console.log("批量删除被选中的元素：", checkNodes);
            for (let i = 0; i < checkNodes.length; i++) {
                catIds.push(checkNodes[i].catId);
                catNames.push(checkNodes[i].name);
            }
            this.$confirm(`是否删除【${catNames}】菜单?`, "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
            }).then(() => {
                this.$http({
                    url: this.$http.adornUrl('/product/category/delete'),
                    method: 'post',
                    data: this.$http.adornData(catIds, false)
                }).then(() => {
                    this.$message({
                        message: "批量删除成功",
                        type: "success"
                    });
                    this.getMenus();
                });
            }).catch(() => {

            })
        },

        // 根据提交类型是add、edit选择调用不同的方法
        submitData() {
            console.log("dialogType：", this.dialogType)
            if (this.dialogType == "add") {
                console.log("添加数据，调用addCategory()")
                this.addCategory();
            }
            if (this.dialogType == "edit") {
                console.log("修改数据，调用editCategory()")
                this.editCategory();
            }
        },

        edit(data) {
            this.title = "修改分类";
            this.dialogFormVisible = true;
            this.dialogType = "edit";
            // 发送请求获取当前节点的最新数据
            this.$http({
                url: this.$http.adornUrl(`/product/category/info/${data.catId}`),
                method: "get",
            }).then(({ data }) => {
                console.log("回显数据", data);
                this.category.name = data.data.name;
                this.category.catId = data.data.catId;
                this.category.icon = data.data.icon;
                this.category.productUnit = data.data.productUnit;
                this.category.parentCid = data.data.parentCid;
            });
        },

        editCategory() {
            // 定义一个结构，只发送这些数据
            var { catId, name, icon, productUnit } = this.category;
            this.$http({
                url: this.$http.adornUrl("/product/category/update"),
                method: "post",
                data: this.$http.adornData({ catId, name, icon, productUnit }, false),
            }).then(({ data }) => {
                this.$message({
                    message: "菜单修改成功",
                    type: "success",
                });
                //关闭对话框
                this.dialogFormVisible = false;
                //刷新出新的菜单
                this.getMenus();
                //设置需要默认展开的菜单
                this.expandedKey = [this.category.parentCid];
            });
        },

        append(data) {
            this.title = "添加分类";
            //显示对话框
            this.dialogFormVisible = true;
            this.dialogType = "add";
            //父id为当前点击数据id
            this.category.parentCid = data.catId;
            //显示层级为当前点击数据下一级
            this.category.catLevel = data.catLevel * 1 + 1;
            // 清空修改后的数据
            this.category.catId = null;
            this.category.name = "";
            this.category.icon = "";
            this.category.productUnit = "";
            this.category.sort = 0;
            this.category.showStatus = 1;
        },

        addCategory() {
            this.$http({
                url: this.$http.adornUrl('/product/category/save'),
                method: 'post',
                data: this.$http.adornData(this.category, false)
            }).then(({ data }) => {
                this.$message({
                    message: "菜单保存成功",
                    type: "success",
                });
                //关闭对话框
                this.dialogFormVisible = false;
                //刷新出新的菜单
                this.getMenus();
                //设置需要默认展开的菜单
                this.expandedKey = [this.category.parentCid];
            });
        },

        remove(node, data) {
            //将删除id传入数组
            var ids = [data.catId];
            //删除前弹出确认框
            this.$confirm(`是否删除【${data.name}】菜单?`, "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
            })
                .then(() => {
                    this.$http({
                        url: this.$http.adornUrl("/product/category/delete"),
                        method: "post",
                        data: this.$http.adornData(ids, false),
                    }).then(({ data }) => {
                        //成功删除后弹出提示
                        this.$message({
                            message: "菜单删除成功",
                            type: "success",
                        });
                        //刷新出新的菜单
                        this.getMenus();
                        //设置需要默认展开的菜单，使得删除后当前目录处于展开的状态
                        this.expandedKey = [node.parent.data.catId];
                    });
                })
                .catch(() => { });

            console.log("remove", node, data);
        },
    },
    //生命周期 - 创建完成（可以访问当前this实例）
    created() {
        this.getMenus();
    },
    //生命周期 - 挂载完成（可以访问DOM元素）
    mounted() {

    },
    beforeCreate() { }, //生命周期 - 创建之前
    beforeMount() { }, //生命周期 - 挂载之前
    beforeUpdate() { }, //生命周期 - 更新之前
    updated() { }, //生命周期 - 更新之后
    beforeDestroy() { }, //生命周期 - 销毁之前
    destroyed() { }, // 生命周期 - 销毁完成
    activated() { }, //如果页面有keep-alive缓存功能，这个函数会触发
}


</script>
<style scoped>
