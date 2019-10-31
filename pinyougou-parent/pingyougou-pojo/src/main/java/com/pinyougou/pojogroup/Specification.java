package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class Specification implements Serializable {


    private TbSpecification specification;

    private List<TbSpecificationOption> specificationOptionList;

    public Specification() {
    }
}
