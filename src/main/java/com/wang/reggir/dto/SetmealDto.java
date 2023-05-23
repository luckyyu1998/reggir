package com.wang.reggir.dto;


import com.wang.reggir.pojo.Setmeal;
import com.wang.reggir.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
