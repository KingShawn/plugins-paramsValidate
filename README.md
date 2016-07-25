# plugins-paramsValidate
<p>在实际项目J2EE开发工作中，在进行方法调用时，不可避免的会对传入的方法参数进行校验，如验证参数是否合法，对于不合法的参数，一般的做法是通过<code>log4j</code>或<code>slf4j</code>来记录错误日志，并返回指定的类型。
但是对于校验的格式却没有统一性和规范性，会导致参数校验错误和日志的缺失，如果在系统框架不熟悉，那么有可能会在后期维护检查上会带来一定程度上的麻烦。
最近设计了使用<code>Spring + AspectJ</code>基于注解方式来实现方法参数校验和日志记录的框架，可以在系统中直接引入此代码使用，也计划做成通用性的jar包形式，在具体实施过程中遇到了技术瓶颈，「文字是指向月亮的手指」，这里仅提供思路和sample code,供大家参考。</p>

<hr />

<h4>注意</h4>


<p>需要配置<code>aspectj</code>自动代理<code>&lt;aop:aspectj-autoproxy proxy-target-class="true">
&lt;/aop:aspectj-autoproxy> </code></p>

<p>p.s.  其中<code>proxy-target-class="true"</code>则表明使用<code>CGLIB</code>做动态代理，如果为false，则使用JDK的动态代理，这2者存在执行速度和对接口类的不同，可自行Google。</p>

<hr />


<h3>使用场景</h3>


<pre><code>

1、首先你的项目必须是基于maven的Spring项目工程

2、在spring的配置文件，如applicationContext.xml中加入
![](http://i.imgur.com/jCxOPei.png)

3、pom.xml文件中增加插件
![](http://i.imgur.com/S4HuC8I.png)


4、在调用方法上使用注解@ParamValidate，默认规则：如果存在参数为空或整型数据为0，则返回null,并且打印出详细的错误日志，如下
    @ParamValidate
    public String test1(String param1, int param2, boolean param3){
       ....
    }
5、自定义参数，如下
     @ParamValidate(returning={@Returning(type=HashMap.class)},fileds = {  
     index=0 表示下面方法的第一个参数,不能为空，最大长度为10,最小长度为3 ,匹配此正则表达式
    @ValidateFiled(index=0 , notNull=true , maxLen = 10 , minLen = 3 ,regStr= "^\\w+@  \\w+\\.com$"))    
    @ValidateFiled(index=1 , notNull=true , maxLen = 5 , minLen = 2 ) }) 
  public String test2(String param1, String param2){
       ....
    }
</code></pre>

