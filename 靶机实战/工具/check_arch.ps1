# Read the PE file
$filePath = ".\FScan_copy.exe"
[Byte[]] $fileBytes = [System.IO.File]::ReadAllBytes($filePath)

# Check for MZ header
if ($fileBytes[0] -eq 0x4D -and $fileBytes[1] -eq 0x5A) {
    # Get PE header offset (at offset 0x3C)
    $peOffset = [BitConverter]::ToInt32($fileBytes, 0x3C)

    # Check PE signature
    $peSignature = [System.Text.Encoding]::ASCII.GetString($fileBytes[$peOffset..($peOffset+3)])
    if ($peSignature -eq "PE\0\0") {
        # Get machine type (at PE header + 4 bytes)
        $machineTypeOffset = $peOffset + 4
        $machineType = [BitConverter]::ToUInt16($fileBytes, $machineTypeOffset)

        switch ($machineType) {
            0x014c { Write-Output "Architecture: x86 (32-bit)" }
            0x8664 { Write-Output "Architecture: x64 (64-bit)" }
            0x0200 { Write-Output "Architecture: Intel Itanium (IA-64)" }
            default { Write-Output "Architecture: Unknown (Machine Type: 0x$($machineType.ToString('X')))" }
        }
    } else {
        Write-Output "Not a valid PE file"
    }
} else {
    Write-Output "Not a valid MZ executable"
}