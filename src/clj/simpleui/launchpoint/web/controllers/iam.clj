(ns simpleui.launchpoint.web.controllers.iam)

(defmacro do-auth [& body]
  `(do
    (assert (:id ~'session))
    ~@body))

(defmacro do-auth-nil [& body]
  `(do
    (assert (:id ~'session))
    ~@body
    nil))
