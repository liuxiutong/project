app.controller('searchController',function($scope,$location,searchService){

	//搜索条件封装对象
	$scope.searchMap = {'keywords':'','category':'','brand':'','spec':{},'price':'',
	'pageNo':1,'pageSize':40,'sortField':'','sort':''};

    //加载查询字符串
	$scope.loadkeywords = function(){
		$scope.searchMap.keywords = $location.search()['keywords'];
		$scope.search();
	};

    //判断关键字是不是品牌
	$scope.keywordsIsBrand = function(){
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
			if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0) {
				//如果包含
				return true;
			}
        }
        return false;
	};

    //设置排序规则
	$scope.sortSearch = function(sortField,sort){
		$scope.searchMap.sortField=sortField;
		$scope.searchMap.sort=sort;
		$scope.search();
	};

//添加搜索项
	$scope.addSearchItem = function(key,value){
		if (key == 'category' || key == 'brand' ||key=="price") {
			$scope.searchMap[key]=value;
		}else {
            $scope.searchMap.spec[key]=value;
		}
       $scope.search();//执行搜索
	};

	//撤销搜索项
	$scope.removeSearchItem = function (key) {

		if (key == 'category' || key == "brand" ||key=="price") {
            //如果是 分类 或 品牌 或 价格
			$scope.searchMap[key]="";
		}else {
            //否则是规格 移除此属性
			delete $scope.searchMap.spec[key];
		}
        $scope.search();//执行搜索
    };


    //构建分页标签(totalPages为总页数)
	$scope.builPageLabel = function () {
		$scope.pageLabel = [];//增加分页栏属性
		var maxPageNo=$scope.resultMap.totalPages;//得到最后页面
		var firstPage =1;//开始页码
		var lastPage=maxPageNo;//截止页码

		$scope.fristDot = true;//前面有。。。
		$scope.lastDot = true;//后面有。。。

		if (lastPage > 5) {//总页面大于5
			if ($scope.searchMap.pageNo<=3){//如果在前三页，分页标签不变，为1—5；
				lastPage=5;//设置前5页
                $scope.fristDot = false;//前面没。。。

			} else if ($scope.searchMap.pageNo >= lastPage - 2) {
				//如果在最后三页，分页标签不变
				firstPage = maxPageNo-4;//设置后5页
                $scope.lastDot = false;//后面没。。。

			}else{
				//中间部分前两页 后两页
				firstPage = $scope.searchMap.pageNo-2;
				lastPage = $scope.searchMap.pageNo+2;
			}
		}else {
            $scope.fristDot = false;//前面没。。。
            $scope.lastDot = false;//后面没。。。

        }
		//循环产生的页码表签
        for (var i = firstPage; i <=lastPage; i++) {
			$scope.pageLabel.push(i);//页数，最多5个值
        }
    }

    //搜索项
    $scope.search=function(){
		//类型转换 stirng转数字类型
		$scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);

        searchService.search($scope.searchMap).success(
            function(response){
                $scope.resultMap=response;
                $scope.builPageLabel();//调用 构建分页标签
            }
        );
    };

	//根据页码查询
	$scope.queryByPage = function (pageNo) {
		//页码验证
		if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
			return;//超出页码范围时不执行
		}
		$scope.searchMap.pageNo = pageNo;
		$scope.search();//调用 搜索
    }

    //判断当前页为第一页
	$scope.isTopPage = function () {
		if ($scope.searchMap.pageNo==1){
			return true;
		} else {
			return false;
		}
    };

    //判断当前页为最后一页
    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        } else {
            return false;
        }
    }



});