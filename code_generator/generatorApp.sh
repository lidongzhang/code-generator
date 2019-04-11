#!/bin/bash

echo '把package好的程序、配置文件、模版等输出到exec目录，在生成前会删除exec目录前，确认吗？(y/n)'
read x
if [ "${x}" = "y" ] || [ "${x}" = "Y" ]; then
    rm -rf app
    mkdir app
    cp target/*.jar app/
    cp -rf target/classes/conf app/
    cp -rf target/classes/templates app/
else
    echo '退出'
fi