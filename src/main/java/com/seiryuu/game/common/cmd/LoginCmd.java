/*
 * # iohao.com . 渔民小镇
 * Copyright (C) 2021 - 2022 double joker （262610965@qq.com） . All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.seiryuu.game.common.cmd;

import com.seiryuu.game.common.MmoModuleCmd;

/**
 * @author Seiryuu
 * @date 2022-03-23
 */
public interface LoginCmd {
    /**
     * 登录主模块
     */
    int cmd = MmoModuleCmd.login_module_cmd;

    /**
     * 登录
     */
    int loginVerify = 0;

    /**
     * 注册
     */
    int registerVerify = 1;
}
