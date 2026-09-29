function FormInput({
  label,
  name,
  value,
  onChange,
  placeholder,
  error,
  required = false,
  type = "text",
}) {
  return (
    <div>
      <label
        className="
block
text-sm
font-medium
"
      >
        {label}

        {required && <span className="text-red-500">*</span>}
      </label>

      <input
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        className={`
w-full
mt-1
px-3
py-2
border
rounded-lg

${error ? "border-red-500" : "border-gray-300"}

`}
      />

      {error && (
        <p
          className="
text-red-500
text-sm
mt-1
"
        >
          {error}
        </p>
      )}
    </div>
  );
}

export default FormInput;
