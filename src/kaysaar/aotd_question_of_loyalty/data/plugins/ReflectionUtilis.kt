package kaysaar.aotd_question_of_loyalty.data.plugins

import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

class ReflectionUtilis {

    companion object {
        private val methodClass = Class.forName("java.lang.reflect.Method", false, Class::class.java.classLoader)
        private val invokeMethodHandle = MethodHandles.lookup().findVirtual(methodClass, "invoke", MethodType.methodType(Any::class.java, Any::class.java, Array<Any>::class.java))
        @JvmStatic
        fun setPrivateVariable(fieldName: String, instanceToModify: Any, newValue: Any?) {
            val fieldClass = Class.forName("java.lang.reflect.Field", false, Class::class.java.classLoader)
            val setMethod = MethodHandles.lookup()
                .findVirtual(fieldClass, "set", MethodType.methodType(Void.TYPE, Any::class.java, Any::class.java))
            val getNameMethod =
                MethodHandles.lookup().findVirtual(fieldClass, "getName", MethodType.methodType(String::class.java))
            val setAcessMethod = MethodHandles.lookup().findVirtual(
                fieldClass,
                "setAccessible",
                MethodType.methodType(Void.TYPE, Boolean::class.javaPrimitiveType)
            )

            val instancesOfFields: Array<out Any> = instanceToModify.javaClass.getDeclaredFields()
            for (obj in instancesOfFields) {
                setAcessMethod.invoke(obj, true)
                val name = getNameMethod.invoke(obj)
                if (name.toString() == fieldName) {
                    setMethod.invoke(obj, instanceToModify, newValue)
                }
            }
        }
        @JvmStatic
        fun getPrivateVariable(fieldName: String, instanceToGetFrom: Any): Any? {
            val fieldClass = Class.forName("java.lang.reflect.Field", false, Class::class.java.classLoader)
            val getMethod = MethodHandles.lookup()
                .findVirtual(fieldClass, "get", MethodType.methodType(Any::class.java, Any::class.java))
            val getNameMethod =
                MethodHandles.lookup().findVirtual(fieldClass, "getName", MethodType.methodType(String::class.java))
            val setAcessMethod = MethodHandles.lookup().findVirtual(
                fieldClass,
                "setAccessible",
                MethodType.methodType(Void.TYPE, Boolean::class.javaPrimitiveType)
            )

            val instancesOfFields: Array<out Any> = instanceToGetFrom.javaClass.declaredFields
            for (obj in instancesOfFields) {
                setAcessMethod.invoke(obj, true)
                val name = getNameMethod.invoke(obj)
                if (name.toString() == fieldName) {
                    return getMethod.invoke(obj, instanceToGetFrom)
                }
            }
            return null
        }
        @JvmStatic
        fun getPrivateVariableFromSuperClass(fieldName: String, instanceToGetFrom: Any): Any? {
            val fieldClass = Class.forName("java.lang.reflect.Field", false, Class::class.java.classLoader)
            val getMethod = MethodHandles.lookup()
                .findVirtual(fieldClass, "get", MethodType.methodType(Any::class.java, Any::class.java))
            val getNameMethod =
                MethodHandles.lookup().findVirtual(fieldClass, "getName", MethodType.methodType(String::class.java))
            val setAcessMethod = MethodHandles.lookup().findVirtual(
                fieldClass,
                "setAccessible",
                MethodType.methodType(Void.TYPE, Boolean::class.javaPrimitiveType)
            )

            val instancesOfFields: Array<out Any> = instanceToGetFrom.javaClass.superclass.declaredFields
            for (obj in instancesOfFields) {
                setAcessMethod.invoke(obj, true)
                val name = getNameMethod.invoke(obj)
                if (name.toString() == fieldName) {
                    return getMethod.invoke(obj, instanceToGetFrom)
                }
            }
            return null
        }
        @JvmStatic
        fun invoke(methodName: String, instance: Any, vararg arguments: Any?, declared: Boolean = false) : Any?
        {
            var method: Any? = null

            val clazz = instance.javaClass
            val args = arguments.map { it!!::class.javaPrimitiveType ?: it::class.java }
            val methodType = MethodType.methodType(Void.TYPE, args)

            if (!declared) {
                method = clazz.getMethod(methodName, *methodType.parameterArray())
            }
            else  {
                method = clazz.getDeclaredMethod(methodName, *methodType.parameterArray())
            }

            return invokeMethodHandle.invoke(method, instance, arguments)
        }
    }




}