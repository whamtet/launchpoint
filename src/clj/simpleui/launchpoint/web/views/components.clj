(ns simpleui.launchpoint.web.views.components)

(defn input [type label name]
  [:div
   [:div.p-1 label]
   [:div.p-1
    [:input.w-full.p-1
     {:type type
      :name name
      :required true
      :placeholder label}]]])

(def text (partial input "text"))
(def email (partial input "email"))
(def password (partial input "password"))

(defn submit [label]
  [:div.p-1
   [:input {:type "submit"
            :class "bg-clj-blue p-1.5 rounded-lg text-white w-24"
            :value label}]])
