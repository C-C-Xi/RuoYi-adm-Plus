package org.dromara.game.domain;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.system.domain.SysConfig;

/**
 * 参数配置业务对象 sys_config
 *
 * @author Michelle.Chung
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class CollectionBo extends BaseEntity {


    /**
     * 集合名称
     */
    @NotBlank(message = "参数名称不能为空")
    @Size(min = 0, max = 100, message = "参数名称不能超过{max}个字符")
    private String collection;

    /**
     * 展示名称
     */
    @NotBlank(message = "参数键名不能为空")
    @Size(min = 0, max = 100, message = "参数键名长度不能超过{max}个字符")
    private String displayName;



}
