### Tangram study

#### Tangram系列学习笔记：

- [Android | Tangram动态页面之路（一）需求背景](https://juejin.im/post/5eb6ae935188256d5a0d9482)
- [Android | Tangram动态页面之路（二）介绍](https://juejin.im/post/5eba9113f265da7bd442576f)
- [Android | Tangram动态页面之路（三）使用](https://juejin.im/post/5ebbe23ff265da7bfc40390c)
- [Android | Tangram动态页面之路（四）vlayout原理](https://juejin.im/post/5ebd467af265da7bd4425bb4)
- [Android | Tangram动态页面之路（五）Tangram原理](https://juejin.im/post/5ebf64ef6fb9a043790e4679)
- [Android | Tangram动态页面之路（六）数据分离](https://juejin.im/post/5ec148465188256d5324e0a3)

#### Tips

- demo2包：
> 将数据从json模板中分离出来
- demo3包：
> 包装`MyTangramEngine`替换继承`TangramActivity`

> 搭建[TangramService服务端](https://github.com/holidayei/TangramService)模拟商城数据，可以使用提供的公网ip，或者本地运行服务端

运行效果：

![](https://tva1.sinaimg.cn/large/007S8ZIlgy1geyp8hh7krj30f019qwu8.jpg)

#### TODO

- card.notifyDataChange没法局部刷新
> 其实还好，只是触发屏幕内可见的几个item重绑定
- 数据分离的方案优化
> 目前采用的是动态合并数据，能否有更好的方案
- 更灵活的[Virtualview](https://github.com/alibaba/Virtualview-Android/blob/master/README-ch.md)