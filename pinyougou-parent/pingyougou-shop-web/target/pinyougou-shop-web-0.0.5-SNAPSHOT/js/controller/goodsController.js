//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, itemCatService, uploadService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        var id = $location.search()['id'];//获取页面传递到的json格式的参数值
        if (id == null) {
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                //向富文本编辑器添加商品介绍
                editor.html($scope.entity.goodsDesc.introduction);
                //显示图片列表
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                //显示扩展属性
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //显示规格
                $scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);

                //查询SKU列表规格列转换
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec =
                        JSON.parse( $scope.entity.itemList[i].spec);

                }
            }
        );
    };
    //根据规格名称和选项名称返回是否被勾选
    $scope.checkAttributeValue = function(specName,optionName){
        var items=$scope.entity.goodsDesc.specificationItems;
        var object = $scope.searchObjectByKey(items,'attributeName',specName);
        if (object == null) {
            return false;
        }else {
            if (object.attributeValue.indexOf(optionName) >= 0) {
                return true;
            }else {
                return false;
            }
        }

    };

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        $scope.entity.goodsDesc.introduction = editor.html();//提取文本编辑器内容
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    alert("保存成功");//提示
                    $scope.entity = {};//清空
                    editor.html('');//清空富文本编辑器
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象
    /*修改 根据商家的ID进行搜索*/
    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //读取模板Id
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId;
            }
        )
    });


    //读取模板Id 更新品牌列表
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate = response;//获取类型模板
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);//品牌列表
                //如果没有ID，则加载模板中的扩展数据
                if ($location.search()['id'] == null) {
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);//扩展属性

                }

            }
        );
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList = response;
            }
        )
    });

    //读取一级分类
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List = response;
            }
        )
    };

    //读取二级分类
    $scope.$watch('entity.goods.category1Id ', function (newValue, oldValue) {
        //根据选择的值，查询二级分类
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat2List = response;
            }
        )
    });
    //读取三级分类
    $scope.$watch('entity.goods.category2Id ', function (newValue, oldValue) {
        //根据选择的值，查询三级分类
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List = response;
            }
        )
    });

    /**
     * 上传图片
     */
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            if (response.success) {//如果上传成功，取出url
                $scope.image_entity.url = response.message;//设置文件地址
            } else {
                alert(response.message);
            }
        }).error(function () {
            alert("上传发生错误");
        })
    }

    $scope.entity = {goods: {}, goodsDesc: {itemImages: []}};//初始化页面实体，和后端结构一致
    //添加图片列表
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //列表中删除图片
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    };

    $scope.entity = {goodsDesc: {itemImages: [], specificationItems: []}};
    //点击更新选中的
    $scope.updateSpecAttribute = function ($event, name, value) {
        var specList = $scope.entity.goodsDesc.specificationItems;
        //判断已选中数组中，是否包含此规格，如果包含，返回此规格Map对象
        var object = $scope.searchObjectByKey(specList, "attributeName", name);
        if (object != null) {
            if ($event.target.checked) {//如果是选中状态
                object.attributeValue.push(value);
            } else {
                //从此规格中删除规格选项
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1);
                //如果规格中已经没有选项，从已选中数组中删除此规格
                if (object.attributeValue.length == 0) {
                    specList.splice(specList.indexOf(object), 1);
                }
            }
        } else {
            specList.push({attributeName: name, attributeValue: [value]});
        }
    }

    /*4.1	SKU列表-变量构建*/
    //创建SKU列表
    $scope.createItemList = function () {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];//初始化
        //items是已选中的规格集合
        var items = $scope.entity.goodsDesc.specificationItems;

        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
        }
    };


    addColumn = function (list, columnName, columnValues) {
        var newList = [];//新的集合
        // list:遍历SKU，第一次只有一条，第二次就等于第一次返回的结果:
        //{"机身内存":"8G","机身内存":"16G"}
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            //遍历当前传递规格的规格选项数组
            for (var j = 0; j < columnValues.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                //SKU表中的spec字段内容：{"机身内存":"16G","网络":"联通3G"}
                newRow.spec[columnName] = columnValues[j];
                newList.push(newRow);
            }
        }
        return newList;

    };

    //审核状态数组
    $scope.status = ['未审核', '审核已通过', '审核未通过', '关闭'];

    //商品分类列表
    $scope.itemCatList = [];
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    $scope.itemCatList[response[i].id] = response[i].name;
                }

            }
        )
    };


});
