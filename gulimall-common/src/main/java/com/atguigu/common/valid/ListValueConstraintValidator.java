package com.atguigu.common.valid;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author zjw
 * @Create 2022/3/30 11:10
 * @Desc
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {

    private Set<Integer> set=new HashSet<Integer>();

    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] value = constraintAnnotation.vals();
        System.out.println("***********" + value);
        if (value != null){
            for (int i : value) {
                System.out.println("***********" + i);
                set.add(i);
            }
        }
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        System.out.println("*******" + set.contains(value));
        return set.contains(value);
    }
}
