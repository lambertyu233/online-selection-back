package com.atguigu.spzx.model.vo.h5;

import com.alibaba.fastjson.JSONArray;
import com.atguigu.spzx.model.entity.product.Product;
import com.atguigu.spzx.model.entity.product.ProductSku;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "商品详情对象")
public class ProductItemVo {

   @Schema(description = "商品sku信息")
   private ProductSku productSku;

   @Schema(description = "商品信息")
   private Product product;

   @Schema(description = "商品轮播图列表")
   private List<String> sliderUrlList;

   @Schema(description = "商品详情图片列表")
   private List<String> detailsImageUrlList;

   @Schema(description = "商品规格信息")
   private JSONArray specValueList;

   @Schema(description = "商品规格对应商品skuId信息")
   private Map<String,Object> skuSpecValueMap;

}
/*
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "productSku": {
            "id": 1,
            "createTime": "2023-05-25 14:21:07",
            "updateTime": "2024-09-03 20:08:07",
            "isDeleted": 0,
            "skuCode": "1_0",
            "skuName": "小米 红米Note10 5G手机 颜色:白色 内存:8G",
            "productId": 1,
            "thumbImg": "http://139.198.127.41:9000/spzx/20230525/665832167-5_u_1 (1).jpg",
            "salePrice": 1999.00,
            "marketPrice": 2019.00,
            "costPrice": 1599.00,
            "stockNum": 96,
            "saleNum": 4,
            "skuSpec": "颜色:白色,内存:8G",
            "weight": "1.00",
            "volume": "1.00",
            "status": null
        },
        "product": {
            "id": 1,
            "createTime": "2023-05-25 14:21:07",
            "updateTime": "2024-09-03 20:08:07",
            "isDeleted": 0,
            "name": "小米 红米Note10 5G手机",
            "brandId": 1,
            "category1Id": 1,
            "category2Id": 2,
            "category3Id": 3,
            "unitName": "个",
            "sliderUrls": "http://139.198.127.41:9000/spzx/20230525/665832167-5_u_1.jpg,http://139.198.127.41:9000/spzx/20230525/665832167-6_u_1.jpg,http://139.198.127.41:9000/spzx/20230525/665832167-4_u_1.jpg,http://139.198.127.41:9000/spzx/20230525/665832167-1_u_1.jpg,http://139.198.127.41:9000/spzx/20230525/665832167-5_u_1 (1).jpg,http://139.198.127.41:9000/spzx/20230525/665832167-3_u_1.jpg\n",
            "specValue": "[{\"key\":\"颜色\",\"valueList\":[\"白色\",\"红色\",\"黑色\"]},{\"key\":\"内存\",\"valueList\":[\"8G\",\"18G\"]}]",
            "status": 1,
            "auditStatus": 1,
            "auditMessage": "审批通过",
            "brandName": null,
            "category1Name": null,
            "category2Name": null,
            "category3Name": null,
            "productSkuList": null,
            "detailsImageUrls": null
        },
        "sliderUrlList": [
            "http://139.198.127.41:9000/spzx/20230525/665832167-5_u_1.jpg",
            "http://139.198.127.41:9000/spzx/20230525/665832167-6_u_1.jpg",
            "http://139.198.127.41:9000/spzx/20230525/665832167-4_u_1.jpg",
            "http://139.198.127.41:9000/spzx/20230525/665832167-1_u_1.jpg",
            "http://139.198.127.41:9000/spzx/20230525/665832167-5_u_1 (1).jpg",
            "http://139.198.127.41:9000/spzx/20230525/665832167-3_u_1.jpg\n"
        ],
        "detailsImageUrlList": [
            "http://139.198.127.41:9000/spzx/20230525/665832167-5_u_1.jpg",
            "http://139.198.127.41:9000/spzx/20230525/665832167-6_u_1.jpg",
            "http://139.198.127.41:9000/spzx/20230525/665832167-4_u_1.jpg",
            "http://139.198.127.41:9000/spzx/20230525/665832167-1_u_1.jpg",
            "http://139.198.127.41:9000/spzx/20230525/665832167-5_u_1 (1).jpg",
            "http://139.198.127.41:9000/spzx/20230525/665832167-3_u_1.jpg"
        ],
        "specValueList": [
            {
                "valueList": [
                    "白色",
                    "红色",
                    "黑色"
                ],
                "key": "颜色"
            },
            {
                "valueList": [
                    "8G",
                    "18G"
                ],
                "key": "内存"
            }
        ],
        "skuSpecValueMap": {
            "颜色:白色,内存:8G": 1,
            "颜色:红色,内存:18G": 4,
            "颜色:黑色,内存:18G": 6,
            "颜色:黑色,内存:8G": 5,
            "颜色:红色,内存:8G": 3,
            "颜色:白色,内存:18G": 2
        }
    }
}
 */