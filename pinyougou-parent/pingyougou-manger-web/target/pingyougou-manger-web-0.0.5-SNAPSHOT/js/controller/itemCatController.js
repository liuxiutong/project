 //控制层 
app.controller('itemCatController' ,function($scope,$controller,itemCatService11){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService11.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService11.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService11.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService11.update( $scope.entity ); //修改  
		}else{
			$scope.entity=$scope.parentId;//赋值Id
			serviceObject=itemCatService11.add( $scope.entity  );//增加
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.findByParentId($scope.parentId);//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService11.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService11.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//根据上级Id显示下级列表
	$scope.findByParentId = function (parentId) {
		itemCatService11.findByParentId(parentId).success(
			function (response) {
				$scope.list = response;
            }
		)
    }

    //面包屑
	$scope.grade=1;//初始级别为1

	//设置级别
	$scope.setGrade=function (valuse) {
		$scope.grade=valuse;
    }

//读取列表
	$scope.selectList=function (p_entity) {
		if ($scope.grade == 1) {//在一级时，下俩级不显
			$scope.entity_1=null;
			$scope.entity_2=null;
		}
        if ($scope.grade == 2) {//在2级时，下1级不显
            $scope.entity_1=p_entity;
            $scope.entity_2=null;
        }
        if ($scope.grade == 3) {//在3级时，
            $scope.entity_2=p_entity;
        }

        $scope.findByParentId(p_entity.id);//查询此级下级列表
    }

    $scope.parentId=0;//上级Id

	//根据ID显示下级列表
	$scope.findByParentId=function (parentId) {
		$scope.parentId=parentId;//记住上级ID
		itemCatService11.findByParentId(parentId).success(
			function (response) {
				$scope.list=response;
            }
		)
    }

    //下拉列表
	$scope.itemCatList={data:[]};

	$scope.findItemCatList= function () {
        itemCatService11.selectByOptionList().success(
        	function (response) {
				$scope.itemCatList={data:response};
            }
		)
    }
});	
